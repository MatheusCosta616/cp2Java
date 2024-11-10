package com.example.cp2_2;

import com.example.cp2_2.src.main.java.br.com.fiap.twoespx.nucleotidio.model.AlignmentRequest;
import com.example.cp2_2.src.main.java.br.com.fiap.twoespx.nucleotidio.model.AlignmentResponse;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.example.cp2_2.src.main.java.br.com.fiap.twoespx.nucleotidio.service.AlignmentService;
import com.example.cp2_2.src.main.java.br.com.fiap.twoespx.nucleotidio.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class Application {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final AlignmentService alignmentService = new AlignmentService();

    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        
        server.createContext("/v1/alignment/distance", new AlignmentHandler());
        server.setExecutor(null);
        server.start();
        
        System.out.println("Servidor iniciado na porta " + port);
    }

    static class AlignmentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if (!"POST".equals(exchange.getRequestMethod())) {
                    sendResponse(exchange, 405, "Método não permitido");
                    return;
                }
                String requestString = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                    .lines()
                    .collect(Collectors.joining("\n"));

                AlignmentRequest request = objectMapper.readValue(requestString, AlignmentRequest.class);

                AlignmentResponse response = alignmentService.calculateDistance(request);

                String responseJson = objectMapper.writeValueAsString(response);

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                
                // Enviar resposta
                sendResponse(exchange, 200, responseJson);

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "Erro interno do servidor: " + e.getMessage());
            }
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(statusCode, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }
} 