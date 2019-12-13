package com.mayankrastogi.cs587.documentmanager;

import com.mayankrastogi.cs587.documentmanager.entities.*;
import com.mayankrastogi.cs587.documentmanager.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

@Slf4j
@SpringBootApplication
public class DocumentManagerApplication {

    private static final String[] paragraphs = {
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Sapien faucibus et molestie ac feugiat sed lectus. Dictumst quisque sagittis purus sit amet. Mauris augue neque gravida in fermentum. Pretium lectus quam id leo in vitae turpis. Pellentesque massa placerat duis ultricies lacus sed turpis tincidunt. Vitae tempus quam pellentesque nec nam aliquam sem. Leo in vitae turpis massa sed elementum. Nulla porttitor massa id neque aliquam vestibulum morbi blandit. Maecenas accumsan lacus vel facilisis volutpat est velit egestas. Tincidunt augue interdum velit euismod in pellentesque massa placerat duis. Lectus magna fringilla urna porttitor. Fusce ut placerat orci nulla pellentesque dignissim enim sit. Tellus elementum sagittis vitae et leo duis ut diam. Consectetur lorem donec massa sapien.",
            "Mi sit amet mauris commodo quis imperdiet massa tincidunt. Urna nunc id cursus metus aliquam eleifend mi in nulla. Nibh tellus molestie nunc non blandit massa enim. Sagittis purus sit amet volutpat consequat mauris. Vivamus arcu felis bibendum ut tristique et egestas. Cursus risus at ultrices mi tempus imperdiet nulla malesuada pellentesque. Ultrices gravida dictum fusce ut placerat orci nulla. Nulla malesuada pellentesque elit eget gravida cum. Nec feugiat nisl pretium fusce id velit ut tortor pretium. Rhoncus dolor purus non enim praesent elementum facilisis leo vel. Ullamcorper eget nulla facilisi etiam. Gravida quis blandit turpis cursus in hac habitasse platea. Proin fermentum leo vel orci porta non pulvinar. Neque vitae tempus quam pellentesque nec nam. Fermentum dui faucibus in ornare quam viverra orci sagittis.",
            "Lobortis scelerisque fermentum dui faucibus in ornare quam viverra orci. Sagittis vitae et leo duis ut diam quam nulla porttitor. Aliquet nec ullamcorper sit amet risus nullam eget felis. Leo vel orci porta non. Sed libero enim sed faucibus turpis in eu. Neque vitae tempus quam pellentesque. Diam in arcu cursus euismod quis viverra nibh. Duis ultricies lacus sed turpis tincidunt id. Egestas pretium aenean pharetra magna ac placerat vestibulum. Penatibus et magnis dis parturient. Leo in vitae turpis massa sed elementum tempus. Arcu felis bibendum ut tristique et egestas quis ipsum suspendisse. Mi ipsum faucibus vitae aliquet nec. Laoreet sit amet cursus sit amet dictum sit amet justo.",
            "Eu feugiat pretium nibh ipsum consequat nisl vel. Blandit libero volutpat sed cras. Faucibus vitae aliquet nec ullamcorper. Proin sed libero enim sed faucibus turpis in eu. Nunc sed blandit libero volutpat sed cras. Vel orci porta non pulvinar neque laoreet suspendisse. Semper risus in hendrerit gravida. Dis parturient montes nascetur ridiculus. Enim eu turpis egestas pretium aenean pharetra. Molestie at elementum eu facilisis sed odio. Ullamcorper dignissim cras tincidunt lobortis feugiat vivamus at augue. Sem fringilla ut morbi tincidunt augue interdum velit. Pellentesque elit ullamcorper dignissim cras tincidunt lobortis feugiat. Tincidunt dui ut ornare lectus sit amet est placerat. Lorem donec massa sapien faucibus. Nunc mi ipsum faucibus vitae. Netus et malesuada fames ac turpis egestas sed tempus.",
            "In massa tempor nec feugiat nisl pretium fusce id velit. Egestas sed sed risus pretium quam vulputate dignissim suspendisse in. At risus viverra adipiscing at in tellus. Porta nibh venenatis cras sed. Tempor id eu nisl nunc. Commodo elit at imperdiet dui accumsan sit. Ullamcorper eget nulla facilisi etiam. Quam nulla porttitor massa id neque aliquam. Quisque non tellus orci ac auctor. Nunc non blandit massa enim nec dui. Condimentum lacinia quis vel eros donec.",
            "Quam viverra orci sagittis eu volutpat odio facilisis. Mauris cursus mattis molestie a iaculis at erat pellentesque adipiscing. Dui accumsan sit amet nulla facilisi. Facilisis leo vel fringilla est ullamcorper eget nulla facilisi etiam. Tristique senectus et netus et malesuada fames ac turpis egestas. Donec adipiscing tristique risus nec feugiat in fermentum. Etiam erat velit scelerisque in dictum non consectetur. Aenean sed adipiscing diam donec. Aenean pharetra magna ac placerat vestibulum lectus mauris. Molestie a iaculis at erat pellentesque adipiscing. Interdum posuere lorem ipsum dolor sit. Facilisi morbi tempus iaculis urna id volutpat lacus laoreet non. Ac felis donec et odio pellentesque diam. Sociis natoque penatibus et magnis dis. Morbi enim nunc faucibus a pellentesque sit amet. Tellus in hac habitasse platea dictumst vestibulum rhoncus. Accumsan lacus vel facilisis volutpat est velit egestas dui id. Ultricies mi eget mauris pharetra et ultrices neque.",
            "Ipsum dolor sit amet consectetur. Tellus elementum sagittis vitae et. Eu sem integer vitae justo eget magna fermentum. Eget magna fermentum iaculis eu non diam phasellus vestibulum. Nisl suscipit adipiscing bibendum est ultricies integer. Habitant morbi tristique senectus et netus et malesuada. Ornare quam viverra orci sagittis eu volutpat odio. Enim eu turpis egestas pretium aenean. Elit ut aliquam purus sit amet. Diam maecenas sed enim ut. Felis imperdiet proin fermentum leo vel orci. Lacus vel facilisis volutpat est velit egestas dui id ornare. Fames ac turpis egestas integer eget aliquet nibh praesent. Aliquet bibendum enim facilisis gravida neque convallis a cras semper. Pretium quam vulputate dignissim suspendisse. A scelerisque purus semper eget duis at tellus.",
            "Rhoncus urna neque viverra justo nec. Sed nisi lacus sed viverra tellus in hac habitasse. Vel pharetra vel turpis nunc eget lorem dolor. Nulla malesuada pellentesque elit eget. Ac felis donec et odio pellentesque diam volutpat commodo sed. Ornare suspendisse sed nisi lacus sed viverra. Leo in vitae turpis massa. Commodo quis imperdiet massa tincidunt nunc. Metus dictum at tempor commodo ullamcorper a. Turpis egestas maecenas pharetra convallis posuere morbi leo. Erat nam at lectus urna duis convallis convallis. Sed nisi lacus sed viverra tellus in hac habitasse. Sit amet nisl purus in mollis. Id consectetur purus ut faucibus pulvinar elementum integer.",
            "Lacus vestibulum sed arcu non. Consectetur purus ut faucibus pulvinar elementum. Turpis in eu mi bibendum. Ullamcorper morbi tincidunt ornare massa eget egestas purus viverra. Enim ut sem viverra aliquet eget sit amet tellus cras. Ut enim blandit volutpat maecenas volutpat blandit aliquam. Aliquam eleifend mi in nulla posuere sollicitudin aliquam. Molestie nunc non blandit massa. Mi sit amet mauris commodo quis imperdiet massa tincidunt nunc. Tincidunt dui ut ornare lectus sit amet est placerat in. Ultrices tincidunt arcu non sodales neque. Convallis tellus id interdum velit laoreet id donec ultrices. Risus feugiat in ante metus. Nisi scelerisque eu ultrices vitae auctor eu augue. At imperdiet dui accumsan sit. Non sodales neque sodales ut. Elit ut aliquam purus sit amet luctus venenatis lectus magna.",
            "In cursus turpis massa tincidunt dui ut ornare. Donec pretium vulputate sapien nec sagittis aliquam malesuada. Massa sed elementum tempus egestas sed sed. Et tortor at risus viverra adipiscing at in. Cursus risus at ultrices mi tempus imperdiet nulla malesuada. Sed faucibus turpis in eu mi bibendum neque egestas. Mauris pharetra et ultrices neque ornare aenean euismod elementum. Etiam non quam lacus suspendisse faucibus interdum posuere lorem ipsum. Et malesuada fames ac turpis egestas maecenas pharetra. Id venenatis a condimentum vitae sapien pellentesque. Interdum consectetur libero id faucibus. Nulla facilisi morbi tempus iaculis urna id volutpat. Ut etiam sit amet nisl purus in. Imperdiet nulla malesuada pellentesque elit.",
            "Pharetra magna ac placerat vestibulum lectus. Tellus pellentesque eu tincidunt tortor aliquam nulla facilisi cras fermentum. Interdum varius sit amet mattis vulputate enim nulla aliquet porttitor. Tincidunt praesent semper feugiat nibh sed pulvinar proin. Sollicitudin ac orci phasellus egestas tellus rutrum tellus pellentesque eu. Aliquam nulla facilisi cras fermentum. Tellus molestie nunc non blandit massa enim. Semper feugiat nibh sed pulvinar. Aliquet risus feugiat in ante metus dictum at tempor. Nisi quis eleifend quam adipiscing vitae proin sagittis. Cras adipiscing enim eu turpis egestas pretium aenean pharetra. Gravida quis blandit turpis cursus in hac habitasse platea. Neque vitae tempus quam pellentesque. Ipsum consequat nisl vel pretium lectus quam id leo. Lobortis elementum nibh tellus molestie nunc.",
            "Nunc faucibus a pellentesque sit amet porttitor eget dolor. Quis varius quam quisque id. Faucibus pulvinar elementum integer enim neque volutpat ac tincidunt vitae. Nunc non blandit massa enim nec dui nunc mattis. Congue eu consequat ac felis donec et odio pellentesque diam. Id venenatis a condimentum vitae sapien pellentesque habitant. Massa ultricies mi quis hendrerit dolor magna eget est. Nulla posuere sollicitudin aliquam ultrices sagittis orci. Aliquet nec ullamcorper sit amet risus nullam. Odio facilisis mauris sit amet massa vitae. Volutpat consequat mauris nunc congue nisi vitae. Pretium quam vulputate dignissim suspendisse in est ante. Condimentum id venenatis a condimentum vitae sapien pellentesque. Facilisis sed odio morbi quis. Diam volutpat commodo sed egestas egestas fringilla phasellus faucibus scelerisque."
    };
    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(DocumentManagerApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(
            UserRepository userRepository,
            LabelRepository labelRepository,
            DocumentRepository documentRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository) {

        return (args) -> {

            log.info("Creating labels...");
            var labelIDs = Arrays.asList("Unclassified", "Confidential", "Secret", "TopSecret");
            labelIDs.stream().map(Label::new)
                    .forEach(labelRepository::save);
            log.info("Labels: " + labelRepository.findAll());

            log.info("Creating Documents...");
            LongStream.range(0, paragraphs.length)
                    .mapToObj(i -> new Document(
                            "Document" + (i + 1) + ".txt",
                            labelRepository.findById((i % 4) + 1).orElse(null),
                            paragraphs[(int) i]
                    ))
                    .forEach(documentRepository::save);
            log.info("Documents: " + documentRepository.findAll());

            log.info("Creating permissions...");
            var permissions = labelIDs
                    .stream()
                    .flatMap(label -> Stream.of("read" + label, "write" + label, "downgrade" + label, "upgrade" + label))
                    .filter(label -> !label.equals("downgradeUnclassified") && !label.equals("upgradeTopSecret"))
                    .map(Permission::new)
                    .collect(Collectors.toList());
            permissionRepository.saveAll(permissions);
            var manageUsersPermission = new Permission("manageUsers");
            permissionRepository.save(manageUsersPermission);
            log.info("Permissions: " + permissionRepository.findAll());

            log.info("Creating roles...");
            for (var id : labelIDs) {
                var userRole = new Role(
                        id.replaceFirst("^.", id.substring(0, 1).toLowerCase()) + "User",
                        getPermissionsForAllLabelsBelow(id, labelRepository, permissionRepository)
                );
                roleRepository.save(userRole);
            }

            var topSecretUser = roleRepository.findById("topSecretUser").get();
            var topSecretAdmin = new Role(
                    "topSecretAdmin",
                    permissionRepository.findById("downgradeTopSecret").get(),
                    permissionRepository.findById("upgradeSecret").get()
            );
            roleRepository.save(topSecretAdmin);

            var secretUser = roleRepository.findById("secretUser").get();
            var secretAdmin = new Role(
                    "secretAdmin",
                    permissionRepository.findById("downgradeSecret").get(),
                    permissionRepository.findById("upgradeConfidential").get()
            );
            roleRepository.save(secretAdmin);

            var confidentialUser = roleRepository.findById("confidentialUser").get();
            var confidentialAdmin = new Role(
                    "confidentialAdmin",
                    permissionRepository.findById("downgradeConfidential").get(),
                    permissionRepository.findById("upgradeUnclassified").get()
            );
            roleRepository.save(confidentialAdmin);

            var systemAdmin = new Role("systemAdmin", manageUsersPermission);
            roleRepository.save(systemAdmin);

            var unclassifiedUser = roleRepository.findById("unclassifiedUser").get();

            log.info("Roles: " + roleRepository.findAll());

            log.info("Creating users...");
            var password = passwordEncoder.encode("test");
            var users = Arrays.asList(
                    new User("Unclassified", "User", "unclassified.user@email.com", password, unclassifiedUser),
                    new User("Confidential", "User", "confidential.user@email.com", password, confidentialUser),
                    new User("Confidential", "Admin", "confidential.admin@email.com", password, confidentialUser, confidentialAdmin),
                    new User("Secret", "User", "secret.user@email.com", password, secretUser),
                    new User("Secret", "Admin", "secret.admin@email.com", password, secretUser, secretAdmin),
                    new User("Top Secret", "User", "top_secret.user@email.com", password, topSecretUser),
                    new User("Top Secret", "Admin", "top_secret.admin@email.com", password, topSecretAdmin),
                    new User("System", "Admin", "system.admin@email.com", password, systemAdmin)
            );
            userRepository.saveAll(users);
            log.info("Users: " + userRepository.findAll());
        };
    }

    private Permission[] getPermissionsForAllLabelsBelow(String labelName, LabelRepository labelRepository, PermissionRepository permissionRepository) {
        var label = labelRepository.findByName(labelName);
        var readPermissions = permissionRepository
                .findAllByIdIn(
                        labelRepository.findAllByIdLessThanEqual(label.getId())
                                .stream()
                                .map(l -> "read" + l.getName())
                                .collect(Collectors.toList())
                );
        var writePermission = permissionRepository.findById("write" + labelName).get();

        readPermissions.add(writePermission);
        var permissionArray = new Permission[readPermissions.size()];
        readPermissions.toArray(permissionArray);
        return permissionArray;
    }
}
