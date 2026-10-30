package com.unla.TPGrupalOO2Hito2Grupo01.repositories;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IPedidoRepository extends JpaRepository<Pedido, Integer> {
    List<Pedido> findByUnidadVentaIdUnidadVenta(Integer idUnidadVenta);
}

