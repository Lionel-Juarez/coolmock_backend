package com.example.hamacasbackend.controllers;

import com.example.hamacasbackend.entidades.Cliente;
import com.example.hamacasbackend.repositorios.ClienteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteRepositorio clienteRepositorio;

    @Autowired
    public ClienteController(ClienteRepositorio clienteRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
    }

    @GetMapping("/")
    public ResponseEntity<List<Cliente>> getAllClientes() {
        List<Cliente> clientes = clienteRepositorio.findByRolOrderByNombreCompletoAsc("CLIENTE");
        return ResponseEntity.ok(clientes);
    }


    @GetMapping("uid/{uid}")
    public ResponseEntity<Cliente> getClienteByUid(@PathVariable("uid") String uid) {
        Optional<Cliente> clienteFound = clienteRepositorio.findByUid(uid);
        return clienteFound.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/nuevoCliente")
    public ResponseEntity<Cliente> createCliente(@RequestBody Cliente cliente) {
        try {
            Optional<Cliente> existingCliente = clienteRepositorio.findByEmail(cliente.getEmail());
            if (existingCliente.isPresent()) {
                Cliente clienteToUpdate = existingCliente.get();
                clienteToUpdate.setNombreCompleto(cliente.getNombreCompleto());
                clienteToUpdate.setNumeroTelefono(cliente.getNumeroTelefono());
                clienteToUpdate.setUid(cliente.getUid());
                Cliente updatedCliente = clienteRepositorio.save(clienteToUpdate);
                return new ResponseEntity<>(updatedCliente, HttpStatus.OK);
            } else {
                Cliente createdCliente = clienteRepositorio.save(cliente);
                return new ResponseEntity<>(createdCliente, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable("id") Long id) {
        Optional<Cliente> clienteFound = clienteRepositorio.findById(id);
        return clienteFound.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/actualizarCliente/{id}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable("id") Long id, @RequestBody Cliente clienteDetails) {
        return clienteRepositorio.findById(id).map(cliente -> {
            cliente.setNombreCompleto(clienteDetails.getNombreCompleto());
            cliente.setNumeroTelefono(clienteDetails.getNumeroTelefono());
            cliente.setEmail(clienteDetails.getEmail());
//            cliente.setUid(clienteDetails.getUid());
            Cliente updatedCliente = clienteRepositorio.save(cliente);
            return new ResponseEntity<>(updatedCliente, HttpStatus.OK);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/eliminarCliente/{id}")
    public ResponseEntity<HttpStatus> deleteCliente(@PathVariable("id") Long id) {
        try {
            clienteRepositorio.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/crearOActualizar")
    public ResponseEntity<Cliente> createOrUpdateCliente(@RequestBody Cliente cliente) {
        Optional<Cliente> existingCliente = clienteRepositorio.findByEmail(cliente.getEmail());
        Cliente savedCliente;
        if (existingCliente.isPresent()) {
            Cliente updateCliente = existingCliente.get();
            updateCliente.setNombreCompleto(cliente.getNombreCompleto());
            updateCliente.setNumeroTelefono(cliente.getNumeroTelefono());
            updateCliente.setRol(cliente.getRol());
            if (cliente.getRol() == null){
                cliente.setRol("CLIENTE");
            }
            updateCliente.setUid(cliente.getUid());
            savedCliente = clienteRepositorio.save(updateCliente);
        } else {
            savedCliente = clienteRepositorio.save(cliente);
        }
        return new ResponseEntity<>(savedCliente, HttpStatus.OK);
    }
}
