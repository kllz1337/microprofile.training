package airhacks.blogpad.posts.control;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import airhacks.blogpad.posts.entity.Post;

public class PostStore {

    @Inject
    @ConfigProperty(name = "root.storage.dir")
    String storageDir;

	Path storageDirectoryPath;

    @PostConstruct
    public void init() {
        this.storageDirectoryPath = Path.of(this.storageDir);
    }

    public void save(Post post) {
        String fileName = post.title;
        String stringified = serialize(post);
        try{
            write(fileName, stringified);
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot save post " + fileName);
        }
    }

    String serialize(Post post) {
        Jsonb jsonb = JsonbBuilder.create();
        return jsonb.toJson(post);
    }


    void write(String fileName, String content) throws IOException {
        Path path = this.storageDirectoryPath.resolve(fileName);
        Files.writeString(path, content);
    }


    public Post read(String fileName) throws IOException {
        String stringified = this.readString(fileName);
        return this.deserialize(stringified);
    }

    Post deserialize(String stringified){
        Jsonb jsonb = JsonbBuilder.create();
        return jsonb.fromJson(stringified, Post.class);
    }
 

    String readString(String fileName) throws IOException {
        Path path = this.storageDirectoryPath.resolve(fileName);
        return Files.readString(path);
    }


    
}