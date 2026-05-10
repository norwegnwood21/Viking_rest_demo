
package ru.mephi.vikingdemo.service;

import org.springframework.stereotype.Service;
import ru.mephi.vikingdemo.model.Viking;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class VikingService {
    private final CopyOnWriteArrayList<Viking> vikings = new CopyOnWriteArrayList<>();
    private final VikingFactory vikingFactory;

    @Autowired
    public VikingService(VikingFactory vikingFactory) {
        this.vikingFactory = vikingFactory;
    }

    public List<Viking> findAll() {
        return List.copyOf(vikings);
    }

    public Viking createRandomViking() {
        Viking viking = vikingFactory.createRandomViking();
        vikings.add(viking);
        return viking;
    }

    public Viking addViking(Viking viking) {
        vikings.add(viking);
        return viking;
    }

    public boolean deleteViking(int index) {
        if (index >= 0 && index < vikings.size()) {
            vikings.remove(index);
            return true;
        }
        return false;
    }

    public Viking updateViking(int index, Viking updatedViking) {
        if (index >= 0 && index < vikings.size()) {
            vikings.set(index, updatedViking);
            return updatedViking;
        }
        return null;
    }
    //массовая генерация викингов
    public void generateMultipleVikings(int count) {
        IntStream.range(0, count).forEach(i -> {
            Viking randomViking = vikingFactory.createRandomViking();
            vikings.add(randomViking);
        });
    }
}