package com.leandro.mazzuchello.ibmtest.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leandro.mazzuchello.ibmtest.entities.People;
import com.leandro.mazzuchello.ibmtest.entities.Simulation;
import com.leandro.mazzuchello.ibmtest.repositories.PeopleRepository;
import com.leandro.mazzuchello.ibmtest.repositories.SimulationRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.mockito.Mockito.when;

@WebMvcTest
public class ResourcesTest {

    private static String cpf = "678.819.549-81";

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private Resources resources;

    @MockBean
    private PeopleRepository peopleRepository;

    @MockBean
    private SimulationRepository simulationRepository;

    @BeforeEach
    public void setup() {
        standaloneSetup(this.resources);
    }

    private People createPeople(String cpf, Boolean hasRestriction) {
        return new People("TESTE", cpf, hasRestriction);
    }

    private Simulation createSimulation(Long id, People people) {
        return new Simulation(id, people);
    }

    // CRIAR OS TESTES A PARTIR DAQUI

    @Test
    public void shouldCreatePeoples_whenICreatepeople() throws JsonProcessingException {
        People people = createPeople(cpf, false);
        String requestBody = mapper.writeValueAsString(people);
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/create-people")
                .then()
                .statusCode(200);
    }

    @Test
    public void shouldCreatePeoples_whenICreatePeopleInvalidCPF() throws JsonProcessingException {
        People people = createPeople("11111111111", false);
        String requestBody = mapper.writeValueAsString(people);
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/create-people")
                .then()
                .statusCode(400);
    }

    @Test
    public void shouldListAllPeople_whenIListAllPeople() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/peoples")
                .then()
                .statusCode(200);
    }

    @Test
    public void shouldDeletePeopleID_whenIDeletePeople() {
        People people = createPeople(cpf, false);
        when(peopleRepository.findById(1L)).thenReturn(Optional.of(people));
        peopleRepository.findAll();
        given()
                .accept(ContentType.JSON)
                .when()
                .delete("/api/delete-people/{id}", 1L)
                .then()
                .statusCode(204);
    }

    @Test
    public void shouldDeletePeopleID_whenIDeleteNoExistentPeople() {
        given()
                .accept(ContentType.JSON)
                .when()
                .delete("/api/delete-people/{id}", 2L)
                .then()
                .statusCode(404);
    }

    @Test
    public void shouldCreateOneSimulation_whenICreateSimulation() throws JsonProcessingException {
        People people = createPeople(cpf, false);
        Simulation simulation = createSimulation(1L, people);
        String requestBody = mapper.writeValueAsString(simulation);
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/create-simulation")
                .then()
                .statusCode(200);
    }
    @Test
    public void shouldCreateOneSimulation_whenCreateSimulationNotFound() throws JsonProcessingException {
       People people = createPeople(cpf, false);
        Simulation simulation = createSimulation(3L, people);
        String requestBody = mapper.writeValueAsString(simulation);
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/create-simulation/4")
                .then()
                .statusCode(404);
    }

    @Test//
    public void shouldListAllSimulation_whenIListAllSimulation() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/simulations")
                .then()
                .statusCode(200);
    }

    @Test//
    public void shouldIConsultoneRestrictionForCpf_whenIListRestrictionForCpf() {
        People people = createPeople(cpf, true);
        when(peopleRepository.findPeopleByCpf(cpf)).thenReturn(people);
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/get-restrictions/" + cpf)
                .then()
                .statusCode(200);
    }
    
    
    @Test
    public void shouldIConsultoneRestrictionForCpf_whenIListRestrictionForCpfNotFound() {
        People people = createPeople("222", false);
        when(peopleRepository.findPeopleByCpf(cpf)).thenReturn(people);
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/get-restrictions/")
                .then()
                .statusCode(404);
    }


    @Test
    public void shouldIConsultSimulationbyCpf_whenIListSimulationbyCpf() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/simulation-by-cpf/" + cpf)
                .then()
                .statusCode(200);
    }
    
    @Test
    public void shouldIConsultSimulationbyCpf_whenIListSimulationbyCpfNotFound() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/simulation-by-cpf/")
                .then()
                .statusCode(404);
    }

    @Test 
    public void shouldIRemoveSimulationById_whenIRemoveSimulationById() {
        given()
                .accept(ContentType.JSON)
                .when()
                .delete("/api/delete-simulation/{id}", 1L)
                .then()
                .statusCode(204);
    }
   
    @Test 
    public void shouldIRemoveSimulationById_whenIRemoveSimulationByIdNotFound() {
        given()
                .accept(ContentType.JSON)
                .when()
                .delete("/api/delete-simulation/")
                .then()
                .statusCode(404);
    }
    @Test
    public void shouldIupdateSimulationById_whenIUpdateSimulationById() throws JsonProcessingException {
        People people = createPeople(cpf, false);
        Simulation simulation = createSimulation(1L, people);
        String requestBody = mapper.writeValueAsString(simulation);
        when(simulationRepository.findById(1L)).thenReturn(Optional.of(simulation));
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/api/update-simulation/{id}", 1L)
                .then()
                .statusCode(204);
    }

    @Test
    public void shouldIupdateSimulationById_whenIUpdateSimulationByNonexistentId() throws JsonProcessingException {
        People people = createPeople(cpf, false);
        Simulation simulation = createSimulation(1L, people);
        String requestBody = mapper.writeValueAsString(simulation);
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/api/update-simulation/{id}", 1L)
                .then()
                .statusCode(404);
    }

}
