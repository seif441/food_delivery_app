package com.system.food_delivery_app.service;

import com.system.food_delivery_app.model.Menu;
import com.system.food_delivery_app.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MenuService {

    private final MenuRepository menuRepository;

    
    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<Menu> findAll() {
        return menuRepository.findAll();
    }

    public Optional<Menu> findById(Long id) {
        return menuRepository.findById(id);
    }

    public Menu save(Menu menu) {
        return menuRepository.save(menu);
    }

    public Menu update(Long id, Menu menuDetails) {
        return menuRepository.findById(id)
                .map(menu -> {
                    menu.setName(menuDetails.getName());
                    menu.setDescription(menuDetails.getDescription());
                    menu.setImageURL(menuDetails.getImageURL());
                    menu.setActive(menuDetails.isActive());
                    return menuRepository.save(menu);
                })
                .orElse(null);
    }

    public boolean delete(Long id) {
        if (menuRepository.existsById(id)) {
            menuRepository.deleteById(id);
            return true;
        }
        return false;
    }
}