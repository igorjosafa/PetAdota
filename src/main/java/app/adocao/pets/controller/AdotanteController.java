package app.adocao.pets.controller;

import app.adocao.pets.repository.AdotanteRepository;
import app.adocao.pets.repository.AdocaoRepository;
import app.adocao.pets.model.Adotante;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador responsável por gerenciar adotantes via interface web.
 */
@Controller
@RequestMapping("/adotantes")
public class AdotanteController {

    @Autowired
    private AdotanteRepository adotanteRepository;

    @Autowired
    private AdocaoRepository adocaoRepository;

    /**
     * Exibe a lista de todos os adotantes cadastrados.
     * View: adotantes/list.html
     */
    @GetMapping
    public String listarAdotantes(Model model) {
        model.addAttribute("adotantes", adotanteRepository.findAll());
        return "adotantes/list";
    }

    /**
     * Exibe o formulário para registrar um novo adotante.
     * View: adotantes/form.html
     * Dados disponíveis no form:
     * - Nome do adotante
     * - Telefone do adotante
     * - Email do adotante 
     * - CPF do adotantes
     */
    @GetMapping("/novo")
    public String mostrarFormNovoAdotante(Adotante adotante, Model model) {
        model.addAttribute("adotante", adotante);
        return "adotantes/form";
    }

    /**
     * Processa o formulário de novo adotante.
     * Redireciona de volta para a lista de adotantes.
     *
     * @param Adotante adotante Objeto adotante preenchido no formulário
     * @param result Resultado da validação do formulário
     * @param model Modelo para passar dados para a view
     */
    @PostMapping("/salvar")
    public String salvarAdotante(@Valid Adotante adotante, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "adotantes/form";
        }
        adotanteRepository.save(adotante);
        return "redirect:/adotantes";
    }

    /**
     * Exibe o formulário para editar um adotante.
     * View: adotantes/form.html
     * Dados disponíveis no form:
     * - Nome do adotante
     * - Telefone do adotante
     * - Email do adotante 
     * - CPF do adotantes
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormEditarAdotante(@PathVariable("id") long id, Model model) {
        Adotante adotante = adotanteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Adotante inválido: " + id));
        model.addAttribute("adotante", adotante);
        return "adotantes/form";
    }

    /**
     * Exclui um adotante existente.
     * Não é possível excluir adotantes que tenham adoções associadas.
     *
     * @param id ID do adotante a ser excluído
     * @param model Modelo para passar dados para a view
     */
    @GetMapping("/deletar/{id}")
    public String deletarAdotante(@PathVariable("id") long id, Model model) {
        Adotante adotante = adotanteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Adotante inválido: " + id));

        if (!adocaoRepository.findByAdotante(adotante).isEmpty()) {
            model.addAttribute("erro", "Não é possível excluir o adotante. Há adoções associadas.");
            model.addAttribute("adotantes", adotanteRepository.findAll());
            return "adotantes/list"; 
        }

        adotanteRepository.delete(adotante);

        return "redirect:/adotantes";
    }
}
