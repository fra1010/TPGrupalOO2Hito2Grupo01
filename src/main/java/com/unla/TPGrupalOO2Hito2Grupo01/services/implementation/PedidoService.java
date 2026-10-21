
package com.unla.TPGrupalOO2Hito2Grupo01.services.implementation;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.*;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.*;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IPedidoService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service("pedidoService")
public class PedidoService implements IPedidoService {

    private final IPedidoRepository pedidoRepository;
    private final IPlatoRepository platoRepository;

    public PedidoService(
            IPedidoRepository pedidoRepository,
            IPlatoRepository platoRepository) {

        this.pedidoRepository = pedidoRepository;
        this.platoRepository = platoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> getAll() {
        return pedidoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> findById(Integer idPedido) {
        return pedidoRepository.findById(idPedido);
    }

    @Override
    @Transactional
    public Pedido insertOrUpdate(Pedido pedido) {

        validarPedido(pedido);
        if (pedido.getIdPedido() != null) {
            throw new IllegalArgumentException(
                    "Este método solamente permite registrar nuevos pedidos"
            );
        }

        for (ItemPedido item : pedido.getItemsPedido()) {

            Plato plato = platoRepository.findById(
                    item.getPlato().getIdPlato()
            ).orElseThrow(() ->
                    new IllegalArgumentException(
                            "El plato no existe")
            );

            if (!plato.isActivo()) {
                throw new IllegalArgumentException(
                        "El plato no está activo");
            }

            if (!plato.getUnidadVenta().getIdUnidadVenta()
                    .equals(pedido.getUnidadVenta().getIdUnidadVenta())) {

                throw new IllegalArgumentException(
                        "El plato no pertenece a la unidad de venta");
            }

            BigDecimal total = plato.getPrecioDeVenta()
                    .multiply(BigDecimal.valueOf(item.getCantidad()));

            item.setPlato(plato);
            item.setPedido(pedido);
            item.setTotalItem(total);
        }

        return pedidoRepository.save(pedido);
    }

    private void validarPedido(Pedido pedido) {

        if (pedido == null) {
            throw new IllegalArgumentException(
                    "El pedido no puede ser nulo");
        }

        if (pedido.getFecha() == null ||
                !pedido.getFecha().equals(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La fecha del pedido debe ser la actual");
        }

        if (pedido.getUnidadVenta() == null ||
                pedido.getUnidadVenta().getIdUnidadVenta() == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una unidad de venta");
        }

        if (pedido.getItemsPedido() == null ||
                pedido.getItemsPedido().isEmpty()) {
            throw new IllegalArgumentException(
                    "El pedido debe contener al menos un ítem");
        }

        for (ItemPedido item : pedido.getItemsPedido()) {

            if (item == null ||
                    item.getPlato() == null ||
                    item.getPlato().getIdPlato() == null) {
                throw new IllegalArgumentException(
                        "Cada ítem debe tener un plato válido");
            }

            if (item.getCantidad() == null ||
                    item.getCantidad() <= 0) {
                throw new IllegalArgumentException(
                        "La cantidad debe ser mayor a cero");
            }
        }
    }
}