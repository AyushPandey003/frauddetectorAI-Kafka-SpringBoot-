package frauddetector.service;

import frauddetector.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmbeddingGenerator {
    private final Random random = new Random();

    public float[] generateEmbedding(Transaction transaction) {
        // For demo purposes, generate random embeddings
        float[] embedding = new float[384]; // Using a standard embedding size
        for (int i = 0; i < embedding.length; i++) {
            embedding[i] = random.nextFloat();
        }
        return embedding;
    }
}