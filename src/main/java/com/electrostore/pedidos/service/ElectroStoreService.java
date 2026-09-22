package com.electrostore.pedidos.service;

import com.electrostore.pedidos.dto.ItemPedidoRequestDTO;
import com.electrostore.pedidos.dto.PedidoRequestDTO;
import com.electrostore.pedidos.entity.DetallePedido;
import com.electrostore.pedidos.entity.Pedido;
import com.electrostore.pedidos.entity.Producto;
import com.electrostore.pedidos.exception.BadRequestException;
import com.electrostore.pedidos.exception.ResourceNotFoundException;
import com.electrostore.pedidos.repository.PedidoRepository;
import com.electrostore.pedidos.repository.ProductoCustomRepositoryImpl;
import com.electrostore.pedidos.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ElectroStoreService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoCustomRepositoryImpl productoCustomRepository;

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Producto obtenerProductoPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));
    }

    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    public List<Producto> buscarProductosPorCategoriaNamed(String categoria) {
        return productoRepository.findByCategoriaNamed(categoria);
    }

    public List<Producto> buscarPorPrecioMaximo(BigDecimal precioMax) {
        return productoRepository.buscarPorPrecioMaximoJPQL(precioMax);
    }

    public List<Producto> buscarPorFiltroAvanzadoEM(String keyword, Integer stockMin) {
        return productoCustomRepository.buscarPorFiltrosAvanzados(keyword, stockMin);
    }

    @Transactional
    public Pedido crearPedido(PedidoRequestDTO request) {
        Pedido pedido = new Pedido();
        pedido.setCliente(request.getCliente());

        BigDecimal montoTotalAcumulado = BigDecimal.ZERO;

        for (ItemPedidoRequestDTO item : request.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + item.getProductoId()));

            if (producto.getStock() < item.getCantidad()) {
                throw new BadRequestException("Stock insuficiente para el producto: " + producto.getNombre() 
                        + ". Stock disponible: " + producto.getStock() + ", solicitado: " + item.getCantidad());
            }

            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
            montoTotalAcumulado = montoTotalAcumulado.add(subtotal);

            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(subtotal);

            pedido.addDetalle(detalle);
        }

        pedido.setMontoTotal(montoTotalAcumulado);
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public Pedido obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con el ID: " + id));
    }
}
