package ch.cern.todo;

import ch.cern.todo.repositories.TaskCategoryRepository;
import ch.cern.todo.entities.TaskCategory;
import ch.cern.todo.errors.BadRequestException;
import ch.cern.todo.errors.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@RestController
public class CategoryController {
	@Autowired
	private TaskCategoryRepository categoryRepository;

	@GetMapping("/")
	public String index() {
		return "Hello world!";
	}
	final String prelude = "/v1";

	@GetMapping(prelude + "/categories/name/{name}")
	public TaskCategory getCategoryByName(@PathVariable("name") String name) {
		return categoryRepository.findByCategoryName(name);
	}

	@GetMapping(prelude + "/categories/{id}")
	public TaskCategory getCategoryById(@PathVariable("id") Long id) throws NotFoundException {
		Optional<TaskCategory> byId = categoryRepository.findById(id);
		if (byId.isEmpty()) {
			throw new NotFoundException("Id " + id + " not found in database.");
		}

		return byId.get();
	}

	@PostMapping(prelude + "/categories")
	public TaskCategory createCategory(@RequestBody TaskCategory category) throws BadRequestException {
		// An empty category does not make sense to order items. This is also
		// ensured by the database, but by checking this early we can easily
		// give an easily readable error message.
		if (Objects.equals(category.getCategoryName(), "") || category.getCategoryName() == null) {
			throw new BadRequestException("categoryName cannot be empty or null");
		}

		try {
			categoryRepository.save(category);
		} catch (DataIntegrityViolationException ex) {
			throw new BadRequestException("Cannot create category " + category.getCategoryName() + " already taken");
		}

		return category;
	}

	@GetMapping(prelude + "/categories")
	public List<TaskCategory> getCategories() {
		return categoryRepository.findAll();
	}

	@DeleteMapping(prelude + "/categories/{id}")
	public String deleteCategoryById(@PathVariable("id") Long id) {
		String msg;
		try {
			categoryRepository.deleteById(id);
			msg = "Successfully deleted category: " + id;
		} catch (EmptyResultDataAccessException ex){
			msg = "Category " + id + " cannot be found";
		}
		return msg;
	}

	@PutMapping(prelude + "/categories")
	public TaskCategory updateCategory(@RequestBody TaskCategory category) throws BadRequestException {
		if (category.getId() == null) throw new BadRequestException("id cannot be null.");

		Optional<TaskCategory> otc = categoryRepository.findById(category.getId());
		if (otc.isEmpty()) throw new BadRequestException("category with id " + category.getId() + " does not exist.");
		TaskCategory tc = otc.get();

		// This is a bit defensive since categoryName MUST be set, but this gives us some additional
		// security if we change our definition. Basically, this tactic avoids accidentally overwriting
		// a non-null element. You can still set the value manually to null by passing the empty string.
		if (category.getCategoryName() == null)
			category.setCategoryName(tc.getCategoryName());

		if (category.getCategoryDescription() == null)
			category.setCategoryDescription(tc.getCategoryDescription());
		categoryRepository.save(category);
		return category;
	}
}
