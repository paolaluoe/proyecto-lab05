package com.electrostore.pedidos.dto;

import java.util.List;
public class PedidoRequestDTO {
private String cliente;
private List<ItemPedidoRequestDTO> items;
public PedidoRequestDTO() {}
public String getCliente() { return cliente; }
public void setCliente(String cliente) { this.cliente = cliente; }
public List<ItemPedidoRequestDTO> getItems() { return items; }
public void setItems(List<ItemPedidoRequestDTO> items) { this.items = items; }
}