/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator 0.14.0.0
 * Changes may cause incorrect behavior and will be lost if the code is
 * regenerated.
 */

package fixtures.requiredoptional.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Product model.
 */
public class Product {
    /**
     * The id property.
     */
    @JsonProperty(required = true)
    private int id;

    /**
     * The name property.
     */
    private String name;

    /**
     * Get the id value.
     *
     * @return the id value
     */
    public int getId() {
        return this.id;
    }

    /**
     * Set the id value.
     *
     * @param id the id value to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the name value.
     *
     * @return the name value
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the name value.
     *
     * @param name the name value to set
     */
    public void setName(String name) {
        this.name = name;
    }

}
