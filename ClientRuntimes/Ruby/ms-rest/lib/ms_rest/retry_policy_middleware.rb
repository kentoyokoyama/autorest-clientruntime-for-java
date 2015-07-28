# Copyright (c) Microsoft Corporation. All rights reserved.
# Licensed under the MIT License. See License.txt in the project root for license information.

module MsRest
  #
  # Class whcih handles retry and token renewal stuff.
  #
  class RetryPolicyMiddleware < Faraday::Response::Middleware
    #
    # Initializes a new instance of the RetryPolicyMiddleware class.
    #
    def initialize(app, options = nil)
      fail ArgumentError, 'options can\'t be nil' if options.nil?
      fail ArgumentError, 'options must contain credentials object' if options[:credentials].nil?
      @times = options[:times] || 5
      @delay = options[:delay] || 0.01
      @credentials = options[:credentials]

      super(app)
    end

    #
    # Verifies whether given response is about authentication token expiration.
    # @param response [Net::HTTPResponse] http response to verify.
    #
    # @return [Bool] true if response is about authentication token expiration, false otherwise.
    def is_token_expired_response(response)
      return false unless response.status == 401

      begin
        response_body = JSON.load(response.body)
        error_code = response_body['error']['code']
        error_message = response_body['error']['message']
      rescue Exception => e
        return false
      end

      return (error_code == 'AuthenticationFailed' && (error_message.start_with?('The access token expiry') || (error_message.start_with?('The access token is missing or invalid'))))
    end

    #
    # Performs request and response processing.
    #
    def call(request_env)
      request_body = request_env[:body]

      begin
        @credentials.sign_request(request_env)
        request_env[:body] = request_body

        @app.call(request_env).on_complete do |response_env|
          if (is_token_expired_response(response_env))
            @credentials.acquire_token()
            @credentials.sign_request(request_env)

            sleep @delay
            fail
          end

          status_code = response_env.status

          if (status_code == 408 || (status_code >= 500 && status_code != 501 && status_code != 505))
            sleep @delay
            fail
          end
        end
      rescue Exception => e
        @times = @times - 1
        retry if @times > 0
      end
    end
  end
end