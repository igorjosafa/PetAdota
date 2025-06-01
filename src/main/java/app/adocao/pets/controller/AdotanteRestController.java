package app.adocao.pets.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import app.adocao.pets.model.Adotante;
import app.adocao.pets.repository.AdotanteRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.http.ResponseEntity;
import app.adocao.pets.repository.AdotanteRepository;
import java.util.List;
import app.adocao.pets.repository.RacaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.MediaType;
import java.io.IOException;
import app.adocao.pets.repository.AdocaoRepository;

/**
 * Controlador REST responsável por gerenciar as operações relacionadas à entidade Adotante.
 */
@RestController
@RequestMapping("/api/adotantes")
public class AdotanteRestController {

    private final AdotanteRepository adotanteRepository;
    private final AdocaoRepository adocaoRepository;

    public AdotanteRestController(AdotanteRepository adotanteRepository, AdocaoRepository adocaoRepository) {
        this.adocaoRepository = adocaoRepository;
        this.adotanteRepository = adotanteRepository;
    }


    /**
     * Lista todas as adotantes disponíveis.
     * Endpoint acessado via GET em /api/adotantes/
     * @return Lista de adotantes
     */
    @GetMapping({"/", ""})
    public List<Adotante> listarAdotantes() {
        return adotanteRepository.findAll();
    }

    /**
     * Busca uma adotante específica pelo seu ID.
     * Endpoint acessado via GET em /api/adocoes/{id}
     * @param id ID da adotante
     * @return Adotante encontrada ou 404
     */
    @GetMapping({"/{id}", "{id}/"})
    public ResponseEntity<Adotante> buscarPorId(@PathVariable Long id) {
        return adotanteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Salva uma nova adotante no banco de dados.
     * 
     * Endpoint acessado via POST em /api/adotantes
     * Parâmetros esperados:
     * <ul>
     *   <li><code>nome</code>: nome do adotante</li>
     *   <li><code>cpf</code>: cpf do adotante</li>
     *   <li><code>email</code>: email do adotante</li>
     * </ul>
     * 
     * @param adotante Objeto Adotante enviado no corpo da requisição
     * @return Adotante salva
     */
    @PostMapping(path={"", "/"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Adotante> salvarAdotante(
        @RequestParam("nome") String nome,
        @RequestParam(value = "cpf", required = false) String cpf,
        @RequestParam(value = "email", required = false) String email
    ) throws IOException {
        if (nome == null || nome.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome é obrigatório");
        }
        if (email != null && !email.isBlank() && !email.contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email inválido");
        }
        // Cria uma nova adotante com os dados fornecidos
        Adotante adotante = new Adotante();
        adotante.setNome(nome);
        adotante.setEmail(email);
        adotante.setCpf(cpf);
        Adotante salva = adotanteRepository.save(adotante);
        return ResponseEntity.ok(salva);
    }

    /**
     * Atualiza uma adotante existente.
     * 
     * Endpoint acessado via PUT em /api/adotantes/{id}
     * Parâmetros opcionais:
     * <ul>
     *   <li><code>nome</code>: nome do adotante</li>
     *   <li><code>cpf</code>: cpf do adotante</li>
     *   <li><code>email</code>: email do adotante</li>
     * </ul>
     * 
     * @param id ID da adotante a ser atualizada
     * @param adotanteAtualizada Dados atualizados da adotante
     * @return Adotante atualizada ou 404
     */
    @PutMapping(path = {"/{id}", "/{id}/"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Adotante> atualizarAdotante(
            @PathVariable Long id,
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "cpf", required = false) String cpf,
            @RequestParam(value = "email", required = false) String email
    ) throws IOException {

        return adotanteRepository.findById(id)
                .map(adotanteExistente -> {

                    if (nome != null) {
                        adotanteExistente.setNome(nome);
                    }

                    if (email != null) {
                        if (!email.isBlank() && !email.contains("@")) {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email inválido");
                        }
                        adotanteExistente.setEmail(email);
                    }   

                    if (cpf != null) {
                        adotanteExistente.setCpf(cpf);
                    }

                    Adotante salvo = adotanteRepository.save(adotanteExistente);
                    return ResponseEntity.ok(salvo);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deleta uma adotante, se não houver raças associadas a ela.
     * Endpoint acessado via DELETE em /api/adocoes/{id}
     * @param id ID da adotante a ser deletada
     * @return Resposta com status 204 se bem-sucedido, 404 se não encontrada ou 409 se houver adoções associadas
     */
    @DeleteMapping({"/{id}", "{id}/"})
    public ResponseEntity<Void> deletarAdotante(@PathVariable Long id) {
        Adotante adotante = adotanteRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!adocaoRepository.findByAdotante(adotante).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível excluir o adotante. Há adoções associadas.");
        }

        adotanteRepository.delete(adotante);
        return ResponseEntity.noContent().build();
    }
}
