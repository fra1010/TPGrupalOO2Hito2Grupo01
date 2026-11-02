package com.unla.TPGrupalOO2Hito2Grupo01.services;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.PedidoDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Pedido;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.UnidadVenta;

import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IPedidoService {

    List<Pedido> getAll();

    Optional<Pedido> findById(Integer idPedido);

    Pedido insertOrUpdate(Pedido pedido);

    Pedido registrarPedido(
            PedidoDTO pedidoDTO,
            UnidadVenta unidadVenta
    );

    List<Pedido> getByUnidad(Integer idUnidadVenta);

    Page<LocalDate> getFechasConPedidos(
            Integer idUnidadVenta,
            int pagina
    );

    List<Pedido> getByUnidadYFecha(
            Integer idUnidadVenta,
            LocalDate fecha
    );

    Optional<Pedido> buscarPorNumeroYUnidad(
            Integer numeroPedido,
            Integer idUnidadVenta
    );
}