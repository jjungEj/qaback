package vaatz.stereotypesdb.qa.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vaatz.stereotypesdb.qa.domain.Model;
import vaatz.stereotypesdb.qa.dto.ModelRequest;
import vaatz.stereotypesdb.qa.repository.ModelRepository;

import java.util.List;

@Service
@Transactional
public class ModelService {

    private final ModelRepository modelRepository;

    public ModelService(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    public List<Model> getAll() {
        return modelRepository.findAll();
    }

    public Model getById(Long id) {
        return modelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 모델을 찾을 수 없습니다."));
    }

    public Model create(ModelRequest request) {
        Model model = new Model();
        model.setName(request.getName());
        model.setDescription(request.getDescription());
        model.setVersion(request.getVersion());
        return modelRepository.save(model);
    }

    public Model update(Long id, ModelRequest request) {
        Model model = getById(id);
        model.setName(request.getName());
        model.setDescription(request.getDescription());
        model.setVersion(request.getVersion());
        return modelRepository.save(model);
    }

    public void delete(Long id) {
        Model model = getById(id);
        modelRepository.delete(model);
    }
}
