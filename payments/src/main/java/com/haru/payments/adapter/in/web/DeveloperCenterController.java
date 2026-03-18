package com.haru.payments.adapter.in.web;

import com.haru.payments.application.dto.ClientResponse;
import com.haru.payments.application.dto.CreateClientRequest;
import com.haru.payments.application.usecase.CreateClientUseCase;
import com.haru.payments.application.usecase.ManageClientUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/developer")
public class DeveloperCenterController {
    private final CreateClientUseCase createClientUseCase;
    private final ManageClientUseCase manageClientUseCase;

    @GetMapping
    public String developerCenterLanding() {
        return "developer/landing";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("request", new CreateClientRequest("", "", ""));
        return "developer/register";
    }

    @PostMapping("/register")
    public String registerClient(@ModelAttribute CreateClientRequest request, Model model) {
        ClientResponse client = createClientUseCase.create(request);
        model.addAttribute("client", client);
        return "developer/api-key-display";
    }

    @GetMapping("/dashboard/{clientId}")
    public String showDashboard(@PathVariable UUID clientId, Model model) {
        ClientResponse client = manageClientUseCase.getClient(clientId);
        model.addAttribute("client", client);
        return "developer/dashboard";
    }

    @PostMapping("/api-key/{clientId}/regenerate")
    public String regenerateApiKey(@PathVariable UUID clientId, Model model) {
        ClientResponse client = manageClientUseCase.regenerateApiKey(clientId);
        model.addAttribute("client", client);
        model.addAttribute("message", "API 키가 재발급되었습니다. 이 키는 다시 표시되지 않으니 안전하게 저장하세요.");
        return "developer/api-key-display";
    }

    @PostMapping("/{clientId}/deactivate")
    @ResponseBody
    public ResponseEntity<Void> deactivateClient(@PathVariable UUID clientId) {
        manageClientUseCase.deactivateClient(clientId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{clientId}/activate")
    @ResponseBody
    public ResponseEntity<Void> activateClient(@PathVariable UUID clientId) {
        manageClientUseCase.activateClient(clientId);
        return ResponseEntity.ok().build();
    }
}
