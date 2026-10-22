package com.unla.TPGrupalOO2Hito2Grupo01.repositories;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.UnidadVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository("unidadVentaRepository")
public interface IUnidadVentaRepository extends JpaRepository<UnidadVenta, Integer> {
    @Query("SELECT u FROM UnidadVenta u WHERE u.responsable.username = :username")
    public abstract UnidadVenta traerUnidadVentaResponsable(@Param("username") String username );
    @Query("""
        SELECT CASE
            WHEN TYPE(u) = FoodTruck THEN
                (CAST(u.superficie AS BigDecimal) * c.costoSuperficies)
                + (CASE WHEN TREAT(u AS FoodTruck).conexion = true
                        THEN c.plusElectricidad ELSE 0bd END)
            WHEN TYPE(u) = PuestoDesarmable THEN
                (CAST(u.superficie AS BigDecimal) * c.costoSuperficies)
                + (TREAT(u AS PuestoDesarmable).tiempo * c.costoMontaje)
            ELSE 0bd
        END
        FROM UnidadVenta u
        JOIN u.festival f
        JOIN f.costo c
        WHERE u = :unidadVenta
        """)
    public BigDecimal calcularCanonUnidad(@Param("unidadVenta") UnidadVenta unidadVenta);

    @Query("SELECT u FROM UnidadVenta u LEFT JOIN FETCH u.empleados WHERE u = :unidadVenta")
    public UnidadVenta traerUnidadVentaConEmpleados(@Param("unidadVenta") UnidadVenta unidadVenta );


    public UnidadVenta findByIdUnidadVenta(int idUnidadVenta);
}
