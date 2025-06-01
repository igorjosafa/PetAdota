package app.adocao.pets.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import app.adocao.pets.model.Pet;
import app.adocao.pets.repository.PetRepository;
import app.adocao.pets.model.Raca;
import app.adocao.pets.repository.RacaRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

/**
 * Controlador REST responsável por gerenciar as operações relacionadas à entidade Pet.
 */
@RestController
@RequestMapping("/api/pets")
public class PetRestController {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private RacaRepository racaRepository;

    /**
     * Lista todos os pets disponíveis para adoção.
     * Endpoint acessado via GET em /api/pets/disponiveis
     * @return Lista de pets
     */
    @GetMapping({"/disponiveis", "/disponiveis/"})
    public List<Pet> listarNaoAdotados() {
        return petRepository.findByAdotadoFalse();
    }

    /**
     * Lista todos os pets cadastrados.
     * Endpoint acessado via GET em /api/pets/
     * @return Lista de pets
     */
    @GetMapping({"/", ""})
    public List<Pet> listarPets() {
        return petRepository.findAll();
    }

    /**
     * Busca um pet específica pelo seu ID.
     * Endpoint acessado via GET em /api/pets/{id}
     * @return Lista de pets
     */
    @GetMapping({"/{id}", "/{id}/"})
    public ResponseEntity<Pet> buscarPorId(@PathVariable Long id) {
        return petRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Salva um novo pet no banco de dados.
     * 
     * Endpoint acessado via POST em /api/pets
     * Parâmetros esperados:
     * <ul>
     *   <li><code>nome</code>: nome do pet</li>
     *   <li><code>idada</code>: nome do pet</li>
     *   <li><code>racaId</code>: id da entidade Raca do pet</li>
     *   <li><code>descricao</code>: descrição do pet</li>
     *   <li><code>foto</code>: foto do pet</li>
     * </ul>
     * 
     * @param especie Objeto Especie enviado no corpo da requisição
     * @return Espécie salva
     */
    @PostMapping(path = {"", "/"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<Pet> salvarPet(
            @RequestParam("nome") String nome,
            @RequestParam("idade") int idade,
            @RequestParam("racaId") Long racaId,
            @RequestParam("descricao") String descricao,
            @RequestParam("foto") MultipartFile foto
        ) throws IOException {
            Raca raca = racaRepository.findById(racaId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Raça inválida"));

            Pet pet = new Pet();
            pet.setNome(nome);
            pet.setIdade(idade);
            pet.setRaca(raca);
            pet.setDescricao(descricao);
            pet.setFoto(foto.getBytes());
            pet.setAdotado(false);

            Pet salvo = petRepository.save(pet);
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
        }

    /**
     * Atualiza um pet existente.
     * 
     * Endpoint acessado via PUT em /api/pets/{id}
     * Parâmetros esperados:
     * <ul>
     *   <li><code>nome</code>: nome do pet</li>
     *   <li><code>idada</code>: nome do pet</li>
     *   <li><code>racaId</code>: id da entidade Raca do pet</li>
     *   <li><code>descricao</code>: descrição do pet</li>
     *   <li><code>foto</code>: foto do pet</li>
     * </ul>
     * 
     * @param id ID do pet a ser atualizada
     * @param petAtualizado Dados atualizados da pet
     * @return Raça atualizada ou 404
     */
    @PutMapping(path = {"/{id}", "/{id}/"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Pet> atualizarPet(
            @PathVariable Long id,
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "idade", required = false) Integer idade,
            @RequestParam(value = "racaId", required = false) Long racaId,
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "foto", required = false) MultipartFile foto
    ) throws IOException {

        return petRepository.findById(id)
                .map(petExistente -> {

                    if (racaId != null) {
                        Raca raca = racaRepository.findById(racaId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Raça inválida"));
                        petExistente.setRaca(raca);
                    }

                    if (nome != null) {
                        petExistente.setNome(nome);
                    }

                    if (idade != null) {
                        petExistente.setIdade(idade);
                    }

                    if (descricao != null) {
                        petExistente.setDescricao(descricao);
                    }

                    try {
                        if (foto != null && !foto.isEmpty()) {
                            petExistente.setFoto(foto.getBytes());
                        }
                    } catch (IOException e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao ler a imagem", e);
                    }

                    Pet salvo = petRepository.save(petExistente);
                    return ResponseEntity.ok(salvo);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deleta um pet.
     * Endpoint acessado via DELETE em /api/pets/{id}
     * @param id ID do pet a ser deletado
     * @return Resposta com status 204 se bem-sucedido ou 404 se não encontrado
     */
    @DeleteMapping({"/{id}", "/{id}/"})
    public ResponseEntity<Void> deletarPet(@PathVariable Long id) {
        Pet pet = petRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        petRepository.delete(pet);
        return ResponseEntity.noContent().build();
    }


    
}
