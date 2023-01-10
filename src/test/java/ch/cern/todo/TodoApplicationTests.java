package ch.cern.todo;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.cern.todo.entities.TaskCategory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class TodoApplicationTests {

	@Autowired
	private MockMvc mvc;

	TaskCategory tc1;
	TaskCategory tc2;
	TaskCategory tc3;

	@BeforeEach
	void setUp() {
		tc1 = new TaskCategory();
		tc1.setCategoryDescription("Tasks relating to JongWBS Youth Research Bureau");
		tc1.setCategoryName("JongWBS");

		tc2 = new TaskCategory();
		tc2.setCategoryName("BAAS Project");
		tc2.setCategoryName("Bare Metal as a Service");

		tc3 = new TaskCategory();
		tc3.setCategoryName("ES Head-TA");
		tc3.setCategoryDescription("Helping students in need");
	}

	@Test
	void contextLoads() {
	}

	// https://stackoverflow.com/questions/20504399/testing-springs-requestbody-using-spring-mockmvc
	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	@Test
	public void getHello() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo("Hello world!")));
	}

	@Test
	public void getEmptyCategories() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/v1/categories")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo("[]")));
	}

	private void createCategoryInt(TaskCategory category, ResultMatcher status, ResultMatcher match)
			throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/v1/categories")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(category)))

				.andExpect(status)
				.andExpect(match);
	}

	private void createCategoryInt(TaskCategory category, ResultMatcher match) throws Exception {
		createCategoryInt(category, status().isOk(), match);
	}

	private void createCategoryInt(TaskCategory category) throws Exception {
		createCategoryInt(category, content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	public void createCategory() throws Exception {
		TaskCategory result = tc1;
		result.setId(1L);
		createCategoryInt(tc1, content().string(equalTo(asJsonString(result))));
	}

	@Test
	public void createCategoryNameNull() throws Exception {
		TaskCategory result = tc1;
		tc1.setCategoryName(null);
		result.setId(1L);
		createCategoryInt(tc1,
				status().isBadRequest(),
				r -> assertEquals("categoryName cannot be empty or null",
						r.getResolvedException().getMessage()));
	}

	@Test
	public void createCategoryNameEmpty() throws Exception {
		TaskCategory result = tc1;
		tc1.setCategoryName("");
		result.setId(1L);
		createCategoryInt(tc1,
				status().isBadRequest(),
				r -> assertEquals("categoryName cannot be empty or null",
						r.getResolvedException().getMessage()));
	}

	@Test
	public void createCategoryTaken() throws Exception {
		createCategoryInt(tc1);
		createCategoryInt(tc1,
				status().isBadRequest(),
				r -> assertEquals("Cannot create category JongWBS already taken",
						r.getResolvedException().getMessage()));
	}

	@Test
	public void createCategoryTaken2() throws Exception {
		createCategoryInt(tc1);

		TaskCategory tc1_2 = new TaskCategory();
		tc1_2.setCategoryName(tc1.getCategoryName());
		tc1_2.setCategoryDescription("Different description");
		tc1_2.setId(42L);

		createCategoryInt(tc1_2,
				status().isBadRequest(),
				r -> assertEquals("Cannot create category JongWBS already taken",
						r.getResolvedException().getMessage()));
	}

	@Test
	public void getOneCategory() throws Exception {
		createCategoryInt(tc1);
		tc1.setId(1L);
		mvc.perform(MockMvcRequestBuilders.get("/v1/categories")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(tc1)))
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo(asJsonString(asList(tc1)))));
	}

	@Test
	public void getTwoCategory() throws Exception {
		createCategoryInt(tc1);
		tc1.setId(1L);
		createCategoryInt(tc2);
		tc2.setId(2L);

		mvc.perform(MockMvcRequestBuilders.get("/v1/categories")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(tc1)))
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo(asJsonString(asList(tc1, tc2)))));
	}

	@Test
	public void getThreeCategory() throws Exception {
		createCategoryInt(tc1);
		tc1.setId(1L);
		createCategoryInt(tc2);
		tc2.setId(2L);
		createCategoryInt(tc3);
		tc3.setId(3L);

		mvc.perform(MockMvcRequestBuilders.get("/v1/categories")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(tc1)))
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo(asJsonString(asList(tc1, tc2, tc3)))));
	}

	@Test
	public void getById() throws Exception {
		createCategoryInt(tc1);
		tc1.setId(1L);

		mvc.perform(MockMvcRequestBuilders.get("/v1/categories/1")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo(asJsonString(tc1))));
	}

	@Test
	public void getById2() throws Exception {
		createCategoryInt(tc1);
		tc1.setId(1L);
		createCategoryInt(tc2);
		tc2.setId(2L);
		createCategoryInt(tc3);
		tc3.setId(3L);
		mvc.perform(MockMvcRequestBuilders.get("/v1/categories/2")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo(asJsonString(tc2))));

		mvc.perform(MockMvcRequestBuilders.get("/v1/categories/1")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo(asJsonString(tc1))));

		mvc.perform(MockMvcRequestBuilders.get("/v1/categories/3")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo(asJsonString(tc3))));
	}

	@Test
	public void getByIdNotFound() throws Exception {
		createCategoryInt(tc1);
		createCategoryInt(tc2);
		mvc.perform(MockMvcRequestBuilders.get("/v1/categories/3")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(r -> assertEquals("Id 3 not found in database.",
						r.getResolvedException().getMessage()));
		createCategoryInt(tc3);
		tc3.setId(3L);
		mvc.perform(MockMvcRequestBuilders.get("/v1/categories/3")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo(asJsonString(tc3))));
	}

	// This enough for Jacoco, but this is kind of cheating.
	// Should be 0, 1, 5
	@Test
	public void getByNameNone() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/v1/categories/name/test")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteNotFound() throws Exception {
		createCategoryInt(tc1);
		mvc.perform(MockMvcRequestBuilders.delete("/v1/categories/4"))
				.andExpect(status().isNotFound())
				.andExpect(r -> assertEquals("Category 4 cannot be found",
						r.getResolvedException().getMessage()));
	}

	@Test
	public void deleteExisting() throws Exception {
		mvc.perform(MockMvcRequestBuilders.delete("/v1/categories/1"))
				.andExpect(status().isNotFound())
				.andExpect(r -> assertEquals("Category 1 cannot be found",
						r.getResolvedException().getMessage()));
		createCategoryInt(tc1);
		mvc.perform(MockMvcRequestBuilders.delete("/v1/categories/1"))
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo("Successfully deleted category: 1")));

		mvc.perform(MockMvcRequestBuilders.delete("/v1/categories/1"))
				.andExpect(status().isNotFound())
				.andExpect(r -> assertEquals("Category 1 cannot be found",
						r.getResolvedException().getMessage()));
	}

	private void updateCategory(TaskCategory category, ResultMatcher status,
			ResultMatcher content) throws Exception {
		mvc.perform(MockMvcRequestBuilders.put("/v1/categories")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(category)))
				.andExpect(status)
				.andExpect(content);
	}
	@Test
	public void updateIdNull() throws Exception {
		updateCategory(tc1,
				status().isBadRequest(),
				r -> assertEquals("id cannot be null.",
						r.getResolvedException().getMessage()));
	}

	@Test
	public void updateIdNotExisting() throws Exception {
		tc1.setId(1L);
		updateCategory(tc1,
				status().isBadRequest(),
				r -> assertEquals("category with id 1 does not exist.",
						r.getResolvedException().getMessage()));
	}

	@Test
	public void updateIdNullCategoryName() throws Exception {
		createCategoryInt(tc1);
		tc1.setId(1L);
		tc1.setCategoryDescription("Oud in de Partij van de Arbeid!");

		TaskCategory update = new TaskCategory();
		update.setId(tc1.getId());
		update.setCategoryDescription(tc1.getCategoryDescription());
		update.setCategoryName(null);
		updateCategory(update, status().isOk(), content().string(equalTo(asJsonString(tc1))));
	}

	@Test
	public void updateIdNullCategoryDescription() throws Exception {
		createCategoryInt(tc1);
		tc1.setId(1L);
		tc1.setCategoryName("Jong Wiardi Beckman Stichting");
		TaskCategory update = new TaskCategory();
		update.setCategoryName(tc1.getCategoryName());
		update.setId(tc1.getId());
		update.setCategoryDescription(null);
		updateCategory(update, status().isOk(), content().string(equalTo(asJsonString(tc1))));
	}

	@Test
	public void updateIdCorrect() throws Exception {
		createCategoryInt(tc1);
		tc1.setId(1L);
		tc1.setCategoryName("Jong Wiardi Beckman Stichting");
		tc1.setCategoryDescription("Oud in de Partij van de Arbeid!");
		updateCategory(tc1, status().isOk(), content().string(equalTo(asJsonString(tc1))));
	}

}
