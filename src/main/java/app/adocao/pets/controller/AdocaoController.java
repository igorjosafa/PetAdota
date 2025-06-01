package app.adocao.pets.controller;

import app.adocao.pets.model.Adocao;
import app.adocao.pets.model.Adotante;
import app.adocao.pets.model.Pet;
import app.adocao.pets.repository.AdocaoRepository;
import app.adocao.pets.repository.AdotanteRepository;
import app.adocao.pets.repository.PetRepository;
import app.adocao.pets.service.AdocaoService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controlador responsável por gerenciar adoções via interface web.
 */
@Controller
@RequestMapping("/adocoes")
public class AdocaoController {

    private final AdocaoRepository adocaoRepository;
    private final PetRepository petRepository;
    private final AdotanteRepository adotanteRepository;
    private final AdocaoService adocaoService;

    public AdocaoController(
        AdocaoRepository adocaoRepository, 
        PetRepository petRepository, 
        AdotanteRepository adotanteRepository, 
        AdocaoService adocaoService
        ) {
        this.adocaoRepository = adocaoRepository;
        this.petRepository = petRepository;
        this.adotanteRepository = adotanteRepository;
        this.adocaoService = adocaoService;
    }

    /**
     * Exibe a lista de todas as adoções cadastradas.
     * View: adocoes/list.html
     */
    @GetMapping
    public String listarAdocoes(Model model) {
        model.addAttribute("adocoes", adocaoRepository.findAll());
        return "adocoes/list"; // você pode criar essa view também
    }
    
    /**
     * Exibe o formulário para registrar uma nova adoção.
     * View: adocoes/form.html
     * Dados disponíveis no form:
     * - Pets disponíveis (não adotados)
     * - Lista de adotantes
     */
    @GetMapping("/nova")
    public String mostrarFormularioAdocao(Adocao adocao, Model model) {
        model.addAttribute("pets", petRepository.findByAdotadoFalse());
        model.addAttribute("adotantes", adotanteRepository.findAll());
        model.addAttribute("adocao", adocao);
        return "adocoes/form";  // criaremos essa view depois, se quiser
    }

    /**
     * Processa o formulário de nova adoção.
     * Usa o serviço para vincular pet e adotante.
     * Redireciona de volta para a lista de adoções.
     *
     * @param petId ID do pet a ser adotado
     * @param adotanteId ID do adotante
     */
    @PostMapping("/salvar")
    public String registrarAdocao(@RequestParam Long petId, @RequestParam Long adotanteId) {
        adocaoService.registrarAdocao(petId, adotanteId);

        return "redirect:/adocoes";
    }

    /**
     * Exclui uma adoção existente e atualiza o status do pet para "não adotado".
     * A lógica de negócio está no serviço `AdocaoService`.
     *
     * @param id ID da adoção a ser excluída
     */
    @GetMapping("/deletar/{id}")
    public String deletarAdocao(@PathVariable("id") long id) {
        adocaoService.deletarAdocao(id);
        return "redirect:/adocoes";
    }
}
