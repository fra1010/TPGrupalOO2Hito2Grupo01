package com.unla.TPGrupalOO2Hito2Grupo01.repositories;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.Plato;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.unla.TPGrupalOO2Hito2Grupo01.dtos.PlatoReporteDTO;


import java.util.Optional;
import java.time.LocalDate;
import java.util.List;

public interface IPedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByUnidadVentaIdUnidadVenta(Integer idUnidadVenta);

    // cada pagina es un dia
    @Query(value = "select distinct p.fecha from Pedido p "
            + "where p.unidadVenta.idUnidadVenta = :idUnidad "
            + "order by p.fecha desc",
            countQuery = "select count(distinct p.fecha) from Pedido p "
                    + "where p.unidadVenta.idUnidadVenta = :idUnidad")
    Page<LocalDate> findFechasConPedidos(@Param("idUnidad") Integer idUnidad,
                                         Pageable pageable);

    // pedidos por dia
    List<Pedido> findByUnidadVentaIdUnidadVentaAndFechaOrderByIdPedidoDesc(
            Integer idUnidadVenta, LocalDate fecha);

    Optional<Pedido> findByIdPedidoAndUnidadVentaIdUnidadVenta(
            Integer idPedido,
            Integer idUnidadVenta
    );

    @Query("SELECT new com.unla.TPGrupalOO2Hito2Grupo01.dtos.PlatoReporteDTO(" +
            "item.plato.idPlato, " +
            "item.plato.nombre, " +
            "SUM(item.cantidad), " +
            "item.plato.precioDeVenta, " +
            "item.plato.costoDePlato) " +
            "FROM Pedido p " +
            "JOIN p.itemsPedido item " +
            "WHERE p.unidadVenta.idUnidadVenta = :idUnidad " +
            "AND p.unidadVenta.festival.idFestival = :idFestival " +
            "GROUP BY item.plato.idPlato, item.plato.nombre, item.plato.precioDeVenta, item.plato.costoDePlato " +
            "ORDER BY SUM(item.cantidad) DESC")
    List<PlatoReporteDTO> findPlatoEstrellaByFestivalAndUnidad(
            @Param("idFestival") Integer idFestival,
            @Param("idUnidad") Integer idUnidad,
            Pageable pageable
    );

}