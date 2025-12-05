package com.system.food_delivery_app.model;
package com.example.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Staff extends User {
    private String staffIdNumber;

    public Staff(String name, String email, String staffIdNumber) {
        super(name, email, "STAFF");
        this.staffIdNumber = staffIdNumber;
    }
}
