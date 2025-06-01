package app.adocao.pets.controller;

import app.adocao.pets.model.Especie;
import app.adocao.pets.model.Raca;
import app.adocao.pets.repository.EspecieRepository;
import app.adocao.pets.repository.RacaRepository;
import app.adocao.pets.service.EspecieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


/**
 * Controlador responsável por gerenciar as espécies via interface web.
 */
@Controller
@RequestMapping("/especies")
public class EspecieController {

    private final EspecieService especieService;

    public EspecieController(EspecieService especieService) {
        this.especieService = especieService;
    }

    @Autowired
    private EspecieRepository especieRepository;


    @Autowired
    private RacaRepository racaRepository;

    /**
     * Exibe a lista de todas as espécies cadastradas.
     * View: adotantes/list.html
     */
    @GetMapping
    public String listarEspecies(Model model) {
        model.addAttribute("especies", especieRepository.findAll());
        return "especies/list";
    }

    /**
     * Exibe o formulário para registrar uma nova espécie.
     * View: especies/form.html
     * Dados disponíveis no form:
     * - Nome da espécie
     */
    @GetMapping("/nova")
    public String mostrarFormNovaEspecie(Especie especie) {
        return "especies/form";
    }

    /**
     * Processa o formulário de nova espécie.
     * Redireciona de volta para a lista de espécies.
     *
     * @param Especie especie Objeto especie preenchido no formulário
     * @param result Resultado da validação do formulário
     * @param model Modelo para passar dados para a view
     */
    @PostMapping("/salvar")
    public String salvarEspecie(@Valid Especie especie, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "especies/form";
        }
        especieService.salvarEspecieCriarRacaSRD(especie);

        return "redirect:/especies";
    }

    /**
     * Exibe o formulário para editar uma espécie.
     * View: especies/form.html
     * Dados disponíveis no form:
     * - Nome da espécie
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormEditarEspecie(@PathVariable("id") long id, Model model) {
        Especie especie = especieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Espécie inválida: " + id));
        model.addAttribute("especie", especie);
        return "especies/form";
    }

    /**
     * Exclui uma especie existente .
     * Não é possível excluir especies que tenham raças associadas.
     *
     * @param id ID do especie a ser excluído
     * @param model Modelo para passar dados para a view
     */
    @GetMapping("/deletar/{id}")
    public String deletarEspecie(@PathVariable("id") long id, Model model) {
        Especie especie = especieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Espécie inválida: " + id));

        if (!racaRepository.findByEspecie(especie).isEmpty()) {
            model.addAttribute("erro", "Não é possível excluir a espécie. Há raças associadas.");
            model.addAttribute("especies", especieRepository.findAll());
            return "especies/list"; 
        }

        especieRepository.delete(especie);

        return "redirect:/especies";
    }
}
