/*
package usyd.mingyi.animalcare;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import usyd.mingyi.animalcare.config.ProjectProperties;
import static org.mockito.ArgumentMatchers.notNull;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@Transactional
class AnimalcareApplicationTests {

    @Autowired
    ProjectProperties projectProperties;
    @Autowired
    private WebApplicationContext wac;
    // Fake an MVC environment. The fake environment will not start tomcat
    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession session; //Get the session after login

    @Before
    public void setUp() { // Register mockmvc before testing, that is, declare MockMvc manually

        mockMvc = MockMvcBuilders.standaloneSetup(wac).build();
    }

    @Test
    @BeforeEach
    public void login() throws Exception {
        String url = "http://localhost:8080/login";
        MediaType JSONtype = MediaType.APPLICATION_JSON;//define data type as json

        //Test for non-existent users
        JSONObject badUser = new JSONObject();
        badUser.put("userName", "Lucas");
        badUser.put("password", "12345678");

        MvcResult BadResult = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(JSONtype)
                .content(String.valueOf(badUser)).accept(JSONtype))
                .andExpect(content().contentType(JSONtype))
                .andExpect(MockMvcResultMatchers.status().is(401))
                .andReturn();

        //Test for existing users
        JSONObject user = new JSONObject();
        user.put("userName", "741917776");
        user.put("password", "123456");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(JSONtype)
                .content(String.valueOf(user)).accept(JSONtype))//Perform the requested
                .andExpect(content().contentType(JSONtype))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andReturn();
        //result 结果用于如果有需要可以添加自定义assert
        session = (MockHttpSession) result.getRequest().getSession(false);

        Assert.assertEquals(1, session.getAttribute("id"));
        Assert.assertEquals("741917776", session.getAttribute("userName"));

    }

    @Test
    public void signUp() throws Exception { //No new user is really added to the database because of @Transactional
        String url = "http://localhost:8080/signup";
        MediaType JSON = MediaType.APPLICATION_JSON;//define data type as json
        JSONObject newUser = new JSONObject();
        newUser.put("userName", "Test");
        newUser.put("email", "12345678@163.com");
        newUser.put("password", "11111111");

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(JSON)
                .content(String.valueOf(newUser)).accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    public void uploadPost() throws Exception {
        String url = "http://localhost:8080/post/newPost";
        MediaType JSON = MediaType.APPLICATION_JSON;//define data type as json
        JSONArray base64Data = notNull();

        JSONObject newPost = new JSONObject();
        newPost.put("postTopic", "TopicTest");
        newPost.put("postContent", "Content Test");
        newPost.put("postTag", "cat");
        newPost.put("base64Data", base64Data);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .session(session)
                .contentType(JSON).content(String.valueOf(newPost)).accept(JSON))
                .andExpect(content().contentType(JSON))//验证响应contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();


        Assert.assertEquals(1, result.getRequest().getSession().getAttribute("id"));
    }

    @Test
    public void getPost() throws Exception {
        String url = "http://localhost:8080/getPost/{postId}";
        int postId = 1;
        MediaType JSON = MediaType.APPLICATION_JSON;//define data type as json

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url, postId)
                .session(session)
                .accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn(); //Return MvcResult

        Assert.assertEquals(1, result.getRequest().getSession().getAttribute("id"));
    }

    @Test
    public void getPostsByUserId() throws Exception {
        String url = "http://localhost:8080/getPostByUserId/{id}";
        int userId = 1;
        MediaType JSON = MediaType.APPLICATION_JSON;//define data type as json

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url, userId)
                .session(session)
                .accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn(); //Return MvcResult

        Assert.assertEquals(1, result.getRequest().getSession().getAttribute("id"));
    }

    @Test
    public void deletePost() throws Exception {
        String url = "http://localhost:8080/post/deletePost/{postId}";
        MediaType JSON = MediaType.APPLICATION_JSON;//define data type as json

        //bad delete
        int BadPostId = 3;
        MvcResult BadResult = mockMvc.perform(MockMvcRequestBuilders.delete(url, BadPostId)
                .session(session)
                .accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Fail to delete post, No such post found"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().is(201))
                .andReturn(); //Return MvcResult

        Assert.assertEquals(1, BadResult.getRequest().getSession().getAttribute("id"));

        //right delete
        int postId = 1;
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete(url, postId)
                .session(session)
                .accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn(); //Return MvcResult

        Assert.assertEquals(1, result.getRequest().getSession().getAttribute("id"));


    }

    @Test
    public void addComment() throws Exception {
        String url = "http://localhost:8080/Post/addComment/{postId}";
        MediaType JSON = MediaType.APPLICATION_JSON;
        int postId = 1;

        JSONObject newComment = new JSONObject();
        newComment.put("commentContent", "JingyuTestComment");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url, postId)
                .session(session)
                .contentType(JSON).content(String.valueOf(newComment)).accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Assert.assertEquals(1, result.getRequest().getSession().getAttribute("id"));
    }

    @Test
    public void getCommentsByPostId() throws Exception {
        String url = "http://localhost:8080/getCommentsByPostId/{postId}";
        int postId = 1;
        MediaType JSON = MediaType.APPLICATION_JSON;//define data type as json

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url, postId)
                .session(session)
                .accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn(); //Return MvcResult

        Assert.assertEquals(1, result.getRequest().getSession().getAttribute("id"));
    }

    @Test
    public void addPet() throws Exception { //No new pet will be added to the database（@Transactional）
        String url = "http://localhost:8080/pet/newPet";
        MediaType JSON = MediaType.APPLICATION_JSON;//define data type as json

        JSONObject newPet = new JSONObject();
        newPet.put("avatarUrl", null);
        newPet.put("petName", "myTestDog");
        newPet.put("category", "dog");
        newPet.put("petImageListArr", null);
        newPet.put("petDescription", "myCuteTestDog");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .session(session)
                .contentType(JSON).content(String.valueOf(newPet)).accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Assert.assertEquals(1, result.getRequest().getSession().getAttribute("id"));

        if (session != null) {
            System.out.println("YESSSSS");
        }
    }

    @Test
    public void getPetList() throws Exception {
        String url = "http://localhost:8080/getPetList";
        MediaType JSON = MediaType.APPLICATION_JSON;//define data type as json

        JSONObject data = new JSONObject();//should be equal to data
        data.put("petId",3);
        data.put("userId", null);
        data.put("petName", "XIXI");
        data.put("age", 2);
        data.put("category", "dog");
        data.put("petImageAddress", "http://localhost:8080/images/dogDefault.jpg");
        data.put("petDescription", null);
        data.put("petImageList", null);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .session(session)
                .accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andReturn(); //Return MvcResult

        Assert.assertEquals(1, result.getRequest().getSession().getAttribute("id"));
    }

    @Test
    public void getPet() throws Exception {
        String url = "http://localhost:8080/pet/{petId}";
        MediaType JSON = MediaType.APPLICATION_JSON;//define data type as json

        //no pet found test
        int BadPetId = 6;
        MvcResult BadResult = mockMvc.perform(MockMvcRequestBuilders.get(url, BadPetId)
                .session(session)
                .accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("No such pet found"))//Validate Json content using Json path
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(MockMvcResultMatchers.status().is(201))
                .andDo(print())
                .andReturn(); //Return MvcResult
        Assert.assertEquals(1, BadResult.getRequest().getSession().getAttribute("id"));

        //exist pet
        int petId = 1;
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url, petId)
                .session(session)
                .accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andReturn(); //Return MvcResult
        Assert.assertEquals(1, result.getRequest().getSession().getAttribute("id"));
    }

    @Test
    public void deletePet() throws Exception {
        String url = "http://localhost:8080/pet/{petId}";
        MediaType JSON = MediaType.APPLICATION_JSON;//define data type as json

        //bad delete
        int BadPetId = 5;
        MvcResult BadResult = mockMvc.perform(MockMvcRequestBuilders.delete(url, BadPetId)
                .session(session)
                .accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Fail to delete for no such pet"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().is(201))
                .andReturn(); //Return MvcResult

        Assert.assertEquals(1, BadResult.getRequest().getSession().getAttribute("id"));

        //right delete
        int petId = 1;
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete(url, petId)
                .session(session)
                .accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn(); //Return MvcResult

        Assert.assertEquals(1, result.getRequest().getSession().getAttribute("id"));
    }


}*/

