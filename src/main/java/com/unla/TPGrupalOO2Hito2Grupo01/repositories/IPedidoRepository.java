package com.unla.TPGrupalOO2Hito2Grupo01.repositories;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.Pedido;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IPedidoRepository
        extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByUnidadVentaIdUnidadVenta(
            Integer idUnidadVenta
    );

    // Cada página representa un día con pedidos.
    @Query(
            value = """
                SELECT DISTINCT p.fecha
                FROM Pedido p
                WHERE p.unidadVenta.idUnidadVenta = :idUnidad
                ORDER BY p.fecha DESC
                """,
            countQuery = """
                SELECT COUNT(DISTINCT p.fecha)
                FROM Pedido p
                WHERE p.unidadVenta.idUnidadVenta = :idUnidad
                """
    )
    Page<LocalDate> findFechasConPedidos(
            @Param("idUnidad") Integer idUnidad,
            Pageable pageable
    );

    // Pedidos de una unidad para un día.
    List<Pedido> findByUnidadVentaIdUnidadVentaAndFechaOrderByIdPedidoDesc(
            Integer idUnidadVenta,
            LocalDate fecha
    );

    Optional<Pedido> findByIdPedidoAndUnidadVentaIdUnidadVenta(
            Integer idPedido,
            Integer idUnidadVenta
    );
}