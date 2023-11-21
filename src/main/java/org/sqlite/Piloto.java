package org.sqlite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
class Piloto{
    private int driverid;
    private String code;
    private String forename;
    private String surname;
    private LocalDate fechaNacimiento;
    private String nationality;
    private String url;
    private Constructors constructors;

    public Piloto(String code, String forename, String surname, LocalDate fechaNacimiento, String nationality, String url, Constructors constructors) {
        this.code = code;
        this.forename = forename;
        this.surname = surname;
        this.fechaNacimiento = fechaNacimiento;
        this.nationality = nationality;
        this.url = url;
        this.constructors = constructors;
    }

    @Override
    public String toString() {
        return "Piloto{" +
                "driverid=" + driverid +
                ", code='" + code + '\'' +
                ", forename='" + forename + '\'' +
                ", surname='" + surname + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", nationality='" + nationality + '\'' +
                ", url='" + url + '\'' +
                ", constructors=" + constructors.toString() +
                '}';
    }
}
