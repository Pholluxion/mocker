package com.smartuis.server.dtos;

/**
 * SimulationDTO is a data transfer object that represents a simulation.
 *
 * @param id    the unique identifier of the simulation
 * @param name  the name of the simulation
 * @param state the current state of the simulation
 */
public record SimulationDTO(String id, String name, String state) {
}