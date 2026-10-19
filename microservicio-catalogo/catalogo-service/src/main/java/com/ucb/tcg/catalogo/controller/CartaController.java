package com.ucb.tcg.catalogo.controller;

import com.ucb.tcg.catalogo.model.Carta;
import com.ucb.tcg.catalogo.repository.CartaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cartas")
public class CartaController {

    private final CartaRepository cartaRepository;

    public CartaController(CartaRepository cartaRepository) {
        this.cartaRepository = cartaRepository;
    }

    // 1. LISTAR TODAS LAS CARTAS (GET)
    @GetMapping
    public List<Carta> obtenerTodas() {
        return cartaRepository.findAll();
    }

    // 2. BUSCAR CARTA POR ID (GET)
    @GetMapping("/{id}")
    public ResponseEntity<Carta> obtenerPorId(@PathVariable Long id) {
        Optional<Carta> carta = cartaRepository.findById(id);
        return carta.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 3. CREAR NUEVA CARTA (POST)
    @PostMapping
    public Carta crearCarta(@RequestBody Carta carta) {
        return cartaRepository.save(carta);
    }

    // 4. ACTUALIZAR CARTA (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<Carta> actualizarCarta(@PathVariable Long id, @RequestBody Carta detallesCarta) {
        Optional<Carta> cartaOptional = cartaRepository.findById(id);
        
        if (cartaOptional.isPresent()) {
            Carta carta = cartaOptional.get();
            carta.setNombre(detallesCarta.getNombre());
            carta.setJuego(detallesCarta.getJuego());
            carta.setPrecio(detallesCarta.getPrecio());
            carta.setStock(detallesCarta.getStock());
            
            Carta cartaActualizada = cartaRepository.save(carta);
            return ResponseEntity.ok(cartaActualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 5. ELIMINAR CARTA (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCarta(@PathVariable Long id) {
        if (cartaRepository.existsById(id)) {
            cartaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}