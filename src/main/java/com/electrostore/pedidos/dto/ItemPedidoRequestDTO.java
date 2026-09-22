package com.electrostore.pedidos.dto;
public class ItemPedidoRequestDTO {
private Long productoId;
private Integer cantidad;
public ItemPedidoRequestDTO() {}
public Long getProductoId() { return productoId; }
public void setProductoId(Long productoId) { this.productoId = productoId; }
public Integer getCantidad() { return cantidad; }
public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}