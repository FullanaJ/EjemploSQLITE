package org.sqlite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Constructors{

    private int constructorid;
    private String constructorRef;
    private String name;
    private String nationality;
    private String url;

    public Constructors(String constructorRef, String name, String nationality, String url) {
        this.constructorRef = constructorRef;
        this.name = name;
        this.nationality = nationality;
        this.url = url;
    }

    @Override
    public String toString() {
        return "Constructors{" +
                "constructorid=" + constructorid +
                ", constructorRef='" + constructorRef + '\'' +
                ", name='" + name + '\'' +
                ", nationality='" + nationality + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}