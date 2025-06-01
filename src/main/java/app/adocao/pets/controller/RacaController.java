package app.adocao.pets.controller;

import app.adocao.pets.model.Raca;
import app.adocao.pets.repository.RacaRepository;
import app.adocao.pets.repository.EspecieRepository;
import app.adocao.pets.repository.PetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador responsável por gerenciar raças via interface web.
 */
@Controller
@RequestMapping("/racas")
public class RacaController {

    @Autowired
    private RacaRepository racaRepository;

    @Autowired
    private EspecieRepository especieRepository;

    @Autowired
    private PetRepository petRepository;

    /**
     * Exibe a lista de todas as raças cadastradas.
     * View: racas/list.html
     */
    @GetMapping
    public String listarRacas(Model model) {
        model.addAttribute("racas", racaRepository.findAll());
        return "racas/list";
    }

    /**
     * Exibe o formulário para registrar uma nova raça.
     * View: racas/form.html
     * Dados disponíveis no form:
     * - Nome da raça
     * - Espécie da raça (seleção de espécies disponíveis)
     */
    @GetMapping("/nova")
    public String mostrarFormNovaRaca(Raca raca, Model model) {
        model.addAttribute("raca", raca);
        model.addAttribute("especies", especieRepository.findAll());
        return "racas/form";
    }

    /**
     * Processa o formulário de nova raça.
     * Redireciona de volta para a lista de raças.
     *
     * @param Raca raca Objeto raca preenchido no formulário
     * @param result Resultado da validação do formulário
     * @param model Modelo para passar dados para a view
     */
    @PostMapping("/salvar")
    public String salvarRaca(@Valid Raca raca, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "racas/form";
        }
        racaRepository.save(raca);
        return "redirect:/racas";
    }

    /**
     * Exibe o formulário para registrar uma nova raça.
     * View: racas/form.html
     * Dados disponíveis no form:
     * - Nome da raça
     * - Espécie da raça (seleção de espécies disponíveis)
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormEditarRaca(@PathVariable("id") long id, Model model) {
        Raca raca = racaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Espécie inválida: " + id));
        model.addAttribute("especies", especieRepository.findAll());
        model.addAttribute("raca", raca);
        return "racas/form";
    }

    /**
     * Exclui uma raça existente .
     *
     * @param id ID da raca a ser excluído
     */
    @GetMapping("/deletar/{id}")
    public String deletarRaca(@PathVariable("id") long id) {
        Raca raca = racaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Espécie inválida: " + id));
        
        if (!petRepository.findByRaca(raca).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível excluir a raça. Há pets associados.");
        }

        racaRepository.delete(raca);
        return "redirect:/racas";
    }
}
