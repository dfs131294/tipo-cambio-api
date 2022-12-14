package com.diego.tipocambio.service;

import com.diego.tipocambio.exception.TipoCambioException;
import com.diego.tipocambio.mapper.TipoCambioMapper;
import com.diego.tipocambio.model.*;
import com.diego.tipocambio.repository.TipoCambioRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class TipoCambioService {

    private final TipoCambioRepository tipoCambioRepository;

    public TipoCambioService(TipoCambioRepository tipoCambioRepository) {
        this.tipoCambioRepository = tipoCambioRepository;
    }

    public CalcularTipoCambioResponse calcular(CalcularTipoCambioRequest calcularTipoCambioRequest) {
        calcularTipoCambioRequest.toUpperCase();

        String monedaOrigen = calcularTipoCambioRequest.getMonedaOrigen();
        String monedaDestino = calcularTipoCambioRequest.getMonedaDestino();

        TipoCambio tipoCambioEntity = tipoCambioRepository.findByMonedaOrigenAndMonedaDestino(monedaOrigen, monedaDestino)
                .orElseThrow(() -> new TipoCambioException("Tipo de cambio no encontrado"));

        BigDecimal montoConTipoCambio = calcularTipoCambioRequest.getMonto()
                .multiply(tipoCambioEntity.getTipoCambio());

        return CalcularTipoCambioResponse.builder()
            .monto(calcularTipoCambioRequest.getMonto())
            .montoConTipoCambio(montoConTipoCambio)
            .monedaOrigen(monedaOrigen)
            .monedaDestino(monedaDestino)
            .tipoCambio(tipoCambioEntity.getTipoCambio())
            .build();
    }

    public List<TipoCambio> listar() {
        return tipoCambioRepository.findAll();
    }

    @Transactional
    public TipoCambioResponse guardar(TipoCambioRequest tipoCambioRequest) {
        tipoCambioRequest.toUpperCase();

        String monedaOrigen = tipoCambioRequest.getMonedaOrigen();
        String monedaDestino = tipoCambioRequest.getMonedaDestino();

        tipoCambioRepository.findByMonedaOrigenAndMonedaDestino(monedaOrigen, monedaDestino)
                .ifPresent(t -> { throw new TipoCambioException("Este tipo de cambio ya existe"); });

        TipoCambio tipoCambio = TipoCambioMapper.MAPPER.dtoToModel(tipoCambioRequest);
        TipoCambio tipoCambioBD = tipoCambioRepository.save(tipoCambio);
        return TipoCambioMapper.MAPPER.modelToDto(tipoCambioBD);
    }

    @Transactional
    public TipoCambioResponse actualizar(TipoCambioRequest tipoCambioRequest, Long id) {
        tipoCambioRequest.toUpperCase();

        TipoCambio tipoCambioEntity = tipoCambioRepository.findById(id)
                .orElseThrow(() -> new TipoCambioException("Tipo de cambio no encontrado"));

        tipoCambioEntity.setMonedaOrigen(null != tipoCambioRequest.getMonedaOrigen() ? tipoCambioRequest.getMonedaOrigen() : tipoCambioEntity.getMonedaOrigen());
        tipoCambioEntity.setMonedaDestino(null != tipoCambioRequest.getMonedaDestino() ? tipoCambioRequest.getMonedaDestino() : tipoCambioEntity.getMonedaDestino());
        tipoCambioEntity.setTipoCambio(null != tipoCambioRequest.getTipoCambio() ? tipoCambioRequest.getTipoCambio() : tipoCambioEntity.getTipoCambio());

        TipoCambio tipoCambioBD = tipoCambioRepository.save(tipoCambioEntity);
        return TipoCambioMapper.MAPPER.modelToDto(tipoCambioBD);
    }

    @Transactional
    public TipoCambioResponse actualizarTipoCambio(TipoCambioRequest tipoCambioRequest) {
        tipoCambioRequest.toUpperCase();

        String monedaOrigen = tipoCambioRequest.getMonedaOrigen();
        String monedaDestino = tipoCambioRequest.getMonedaDestino();

        TipoCambio tipoCambioEntity = tipoCambioRepository.findByMonedaOrigenAndMonedaDestino(monedaOrigen, monedaDestino)
                .orElseThrow(() -> new TipoCambioException("Tipo de cambio no encontrado"));

        tipoCambioEntity.setTipoCambio(null != tipoCambioRequest.getTipoCambio() ? tipoCambioRequest.getTipoCambio() : tipoCambioEntity.getTipoCambio());

        TipoCambio tipoCambioBD = tipoCambioRepository.save(tipoCambioEntity);
        return TipoCambioMapper.MAPPER.modelToDto(tipoCambioBD);
    }

}
