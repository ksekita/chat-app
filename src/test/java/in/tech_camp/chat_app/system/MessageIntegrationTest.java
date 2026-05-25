package in.tech_camp.chat_app.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import in.tech_camp.chat_app.ChatAppApplication;
import in.tech_camp.chat_app.ImageUrl;
import in.tech_camp.chat_app.entity.RoomEntity;
import in.tech_camp.chat_app.entity.RoomUserEntity;
import in.tech_camp.chat_app.entity.UserEntity;
import in.tech_camp.chat_app.factories.MessageFormFactory;
import in.tech_camp.chat_app.factories.RoomFormFactory;
import in.tech_camp.chat_app.factories.UserFormFactory;
import in.tech_camp.chat_app.form.MessageForm;
import in.tech_camp.chat_app.form.RoomForm;
import in.tech_camp.chat_app.form.UserForm;
import in.tech_camp.chat_app.repository.MessageRepository;
import in.tech_camp.chat_app.entity.MessageEntity;
import in.tech_camp.chat_app.repository.RoomRepository;
import in.tech_camp.chat_app.repository.RoomUserRepository;
import in.tech_camp.chat_app.service.UserService;
import in.tech_camp.chat_app.support.LoginSupport;

@ActiveProfiles("test")
@SpringBootTest(classes = ChatAppApplication.class)
@AutoConfigureMockMvc
public class MessageIntegrationTest {

  @Autowired
  private UserService userService;

  @Autowired
  private RoomRepository roomRepository;

  @Autowired
  private RoomUserRepository roomUserRepository;

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private ImageUrl imageUrl;

  @Autowired
  private MockMvc mockMvc;

  private UserForm userForm1;
  private UserForm userForm2;
  private RoomForm roomForm;
  private UserEntity userEntity1;
  private UserEntity userEntity2;
  private RoomEntity roomEntity;

  private MessageForm messageForm;
  
  @BeforeEach
  public void setup() {
    userForm1 = UserFormFactory.createUser();
    userEntity1 = new UserEntity();
    userEntity1.setEmail(userForm1.getEmail());
    userEntity1.setName(userForm1.getName());
    userEntity1.setPassword(userForm1.getPassword());
    userService.createUserWithEncryptedPassword(userEntity1);

    userForm2 = UserFormFactory.createUser();
    userEntity2 = new UserEntity();
    userEntity2.setEmail(userForm2.getEmail());
    userEntity2.setName(userForm2.getName());
    userEntity2.setPassword(userForm2.getPassword());
    userService.createUserWithEncryptedPassword(userEntity2);

    roomForm = RoomFormFactory.createRoom();
    roomEntity = new RoomEntity();
    roomEntity.setName(roomForm.getName());
    roomRepository.insert(roomEntity);

    RoomUserEntity roomUserEntity = new RoomUserEntity();
    roomUserEntity.setRoom(roomEntity);
    roomUserEntity.setUser(userEntity1);
    roomUserRepository.insert(roomUserEntity);
    roomUserEntity.setRoom(roomEntity);
    roomUserEntity.setUser(userEntity2);
    roomUserRepository.insert(roomUserEntity);

    messageForm = MessageFormFactory.createMessage();
  }

  @AfterEach
  public void cleanup() throws IOException {
    Path directoryPath = Paths.get(imageUrl.getImageUrl());

    // ディレクトリが存在することを確認
    if (Files.exists(directoryPath) && Files.isDirectory(directoryPath)) {
      // ディレクトリ内のすべてのファイルを削除
      try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directoryPath)) {
        for (Path filePath : directoryStream) {
          Files.deleteIfExists(filePath);
        }
      }
    }
  }

  @Test
  public void メッセージの送信に失敗する() throws Exception {
    // サインインする
    MockHttpSession session = LoginSupport.login(mockMvc, userForm1);
    // 作成されたチャットルームへ遷移する
    mockMvc.perform(get("/rooms/{roomId}/messages", roomEntity.getId()).session(session))
    .andExpect(status().isOk())
    .andExpect(view().name("messages/index"));

    long initialCount = messageRepository.count();
    // 空のメッセージを投稿し、元のページに戻ってくることを確認する
        mockMvc.perform(post("/rooms/{roomId}/messages", roomEntity.getId()).session(session).with(csrf())
            .param("content", ""))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/rooms/" + roomEntity.getId() + "/messages"));
    // メッセージの数が変化しないことを確認する
    long afterCount = messageRepository.count();
    assertEquals(initialCount, afterCount);
  }

 @Test
  public void テキストの投稿に成功する() throws Exception {
    // サインインする
    MockHttpSession session = LoginSupport.login(mockMvc, userForm1);

    // 作成されたチャットルームへ遷移する
    mockMvc.perform(get("/rooms/{roomId}/messages", roomEntity.getId()).session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("messages/index"));
    
    long initialCount = messageRepository.count();

    // テキストメッセージを投稿し、チャットルームに遷移していることを確認する
    mockMvc.perform(post("/rooms/{roomId}/messages", roomEntity.getId()).session(session).with(csrf())
            .param("content", messageForm.getContent()))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/rooms/" + roomEntity.getId() + "/messages"));

    // メッセージの数が1増加していることを確認する
    long afterCount = messageRepository.count();
    assertEquals(initialCount + 1, afterCount);

    // チャットルームには先ほどの投稿が存在することを確認する（テキスト）
    mockMvc.perform(get("/rooms/{roomId}/messages", roomEntity.getId()).session(session))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString(messageForm.getContent())));    
  }

  @Test
  public void 画像の投稿に成功する() throws Exception {
    // サインインする
    MockHttpSession session = LoginSupport.login(mockMvc, userForm1);
    // 作成されたチャットルームへ遷移する
    mockMvc.perform(get("/rooms/{roomId}/messages",roomEntity.getId()).session(session))
   .andExpect(status().isOk())
   .andExpect(view().name("messages/index"));

   long initialCount = messageRepository.count();

    // 画像を投稿し、チャットルームに遷移していることを確認する
    mockMvc.perform(multipart("/rooms/{roomId}/messages",roomEntity.getId())
  .file((MockMultipartFile)messageForm.getImage())
  .session(session).with(csrf()))
  .andExpect(status().isFound())
  .andExpect(redirectedUrl("/rooms/" + roomEntity.getId() + "/messages"));

  // メッセージの数が1増加していることを確認する
  long afterCount = messageRepository.count();
  assertEquals(initialCount + 1, afterCount);
  // チャットルームには先ほどの投稿が存在することを確認する（画像）
  List<MessageEntity> messageList = messageRepository.findByRoomId(roomEntity.getId());
  MessageEntity message = messageList.get(0);

 MvcResult pageResult = mockMvc.perform(get("/rooms/{roomId}/messages", roomEntity.getId()).session(session))
  .andReturn();
String pageContent = pageResult.getResponse().getContentAsString();
Document document = Jsoup.parse(pageContent);
Element divElement = document.selectFirst("img[src='" + message.getImage()+"']");
assertNotNull(divElement);
  }

  @Test
  public void テキストと画像の投稿に成功する() throws Exception {
    // サインインする
    MockHttpSession session = LoginSupport.login(mockMvc, userForm1);

    // 作成されたチャットルームへ遷移する
    mockMvc.perform(get("/rooms/{roomId}/messages", roomEntity.getId()).session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("messages/index"));

    long initialCount = messageRepository.count(); 

    // テキストと画像のメッセージを投稿し、チャットルームに遷移していることを確認する
    mockMvc.perform(multipart("/rooms/{roomId}/messages", roomEntity.getId())
            .file((MockMultipartFile)messageForm.getImage())
            .param("content", messageForm.getContent())
            .session(session).with(csrf()))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/rooms/" + roomEntity.getId() + "/messages"));

    // メッセージの数が1増加していることを確認する
    long afterCount = messageRepository.count();
    assertEquals(initialCount + 1, afterCount);

    // チャットルームには先ほどの投稿が存在することを確認する（テキスト）
    mockMvc.perform(get("/rooms/{roomId}/messages", roomEntity.getId()).session(session))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString(messageForm.getContent())));    

    // チャットルームには先ほどの投稿が存在することを確認する（画像）
    List<MessageEntity> messageList = messageRepository.findByRoomId(roomEntity.getId());
    MessageEntity message = messageList.get(0);

    MvcResult pageResult = mockMvc.perform(get("/rooms/{roomId}/messages", roomEntity.getId()).session(session))
                                    .andReturn();
    String pageContent = pageResult.getResponse().getContentAsString();
    Document document = Jsoup.parse(pageContent);
    Element divElement = document.selectFirst("img[src='"+ message.getImage()+"']");
    assertNotNull(divElement);
  }
}