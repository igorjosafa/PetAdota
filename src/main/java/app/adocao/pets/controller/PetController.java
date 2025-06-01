package app.adocao.pets.controller;

import app.adocao.pets.model.Pet;
import app.adocao.pets.repository.PetRepository;
import app.adocao.pets.repository.EspecieRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Controlador responsável por gerenciar pets via interface web.
 */
@Controller
@RequestMapping("/pets")
public class PetController {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private EspecieRepository especieRepository;

    /**
     * Exibe a lista de todos os pets cadastradas.
     * View: pets/list.html
     */
    @GetMapping
    public String listarPets(Model model) {
        model.addAttribute("pets", petRepository.findAll());
        return "pets/list";  // Nome da view Thymeleaf
    }

    /**
     * Exibe o formulário para registrar um novo pet.
     * View: pets/form.html
     * Dados disponíveis no form:
     * - Nome do pet
     * - Espécie do pet (seleção de espécies disponíveis)
     * - Raça do pet
     * - Idade do pet
     * - Descrição do pet
     * - Foto do pet
     */
    @GetMapping("/novo")
    public String mostrarFormNovoPet(Pet pet, Model model) {
        model.addAttribute("especies", especieRepository.findAll());
        return "pets/form";
    }

    /**
     * Processa o formulário de nova pets.
     * Redireciona de volta para a lista de pets.
     *
     * @param Pet pet Objeto pet preenchido no formulário
     * @param fotoArquivo foto do pet enviada como MultipartFile
     * @param result Resultado da validação do formulário
     * @param model Modelo para passar dados para a view
     */
    @PostMapping("/salvar")
    public String salvarPet(
        @Valid Pet pet,
        @RequestParam("fotoArquivo") MultipartFile foto,
        BindingResult result,
        Model model
        ) throws IOException {
            if (!foto.isEmpty()) {
                pet.setFoto(foto.getBytes());
            }

            if (result.hasErrors()) {
                model.addAttribute("especies", especieRepository.findAll());
                return "pets/form";
            }
            petRepository.save(pet);
            return "redirect:/pets";
    }

    /**
     * Exibe o formulário para editar um pet.
     * View: pets/form.html
     * Dados disponíveis no form:
     * - Nome do pet
     * - Espécie do pet (seleção de espécies disponíveis)
     * - Raça do pet
     * - Idade do pet
     * - Descrição do pet
     * - Foto do pet
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormEditarPet(@PathVariable("id") long id, Model model) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pet inválido: " + id));
        model.addAttribute("pet", pet);
        model.addAttribute("especies", especieRepository.findAll());
        return "pets/form";
    }

    /**
     * Exclui um pet existente .
     *
     * @param id ID do pet a ser excluído
     */
    @GetMapping("/deletar/{id}")
    public String deletarPet(@PathVariable("id") long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pet inválido: " + id));
        petRepository.delete(pet);
        return "redirect:/pets";
    }

    /**
     * Detalha um pet existente.
     *
     * @param id ID do pet
     */
    @GetMapping("/detalhes/{id}")
    public String mostrarDetalhesPet(@PathVariable("id") long id, Model model) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pet inválido: " + id));
        model.addAttribute("pet", pet);
        return "pets/details";
    }

    /**
     * Exibe a foto de um pet.
     *
     * @param id ID do pet
     */
    @GetMapping("/foto/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> exibirFoto(@PathVariable Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pet inválido: " + id));

        byte[] imagem = pet.getFoto();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imagem, headers, HttpStatus.OK);
    }

}
