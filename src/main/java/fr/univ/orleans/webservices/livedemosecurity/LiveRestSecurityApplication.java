package fr.univ.orleans.webservices.livedemosecurity;

import fr.univ.orleans.webservices.livedemosecurity.dto.MessageDTO;
import fr.univ.orleans.webservices.livedemosecurity.dto.UtilisateurDTO;
import fr.univ.orleans.webservices.livedemosecurity.modele.Message;
import fr.univ.orleans.webservices.livedemosecurity.modele.Utilisateur;
import fr.univ.orleans.webservices.livedemosecurity.service.Services;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableWebFlux
public class LiveRestSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(LiveRestSecurityApplication.class, args);
    }

    // configuration du mapper Message <-> MessageDTO
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(Message.class, MessageDTO.class).addMappings(
                mapper -> {
                    mapper.map(src->src.getUtilisateur().getLogin(), MessageDTO::setUtilisateur);
                }
        );
        Converter<List<Message>,List<Long>> toListIds =
                ctx -> ctx.getSource().stream().map(Message::getId).collect(Collectors.toList());
        modelMapper.typeMap(Utilisateur.class, UtilisateurDTO.class).addMappings(
                mapper -> {
                    mapper.using(toListIds).map(Utilisateur::getMessages,UtilisateurDTO::setMessagesIds);
                }
        );

        return modelMapper;
    }

    // init des users en BD
    @Bean
    CommandLineRunner initialisation(Services services) {
        return args -> {
            Utilisateur fred = new Utilisateur("fred","fred", false);
            Message post = new Message(null,"hello world !",fred);
            services.saveUtilisateur(fred);
            services.saveMessage(post);

            services.saveUtilisateur(new Utilisateur("admin","admin", true));
        };
    }

}
