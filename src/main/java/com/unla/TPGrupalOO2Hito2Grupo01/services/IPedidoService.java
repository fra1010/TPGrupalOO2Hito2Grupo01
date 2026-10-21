
package com.unla.TPGrupalOO2Hito2Grupo01.services;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.Pedido;

import java.util.List;
import java.util.Optional;

public interface IPedidoService {

    List<Pedido> getAll();

    Optional<Pedido> findById(Integer idPedido);

    Pedido insertOrUpdate(Pedido pedido);
}