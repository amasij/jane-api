package com.jane;

import com.jane.entity.Setting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {


    private final SettingRepository settingRepository;

    @Transactional
    @Override
    public String getString(String name, String value) {
        return getString(name).orElseGet(() -> {
            Setting setting = new Setting();
            setting.setName(name);
            setting.setValue(value);
            settingRepository.save(setting);
            return value;
        });
    }

    @Transactional
    @Override
    public String getString(String name, Supplier<? extends String> value) {
        return getString(name).orElseGet(() -> {
            Setting setting = new Setting();
            setting.setName(name);
            setting.setValue(value.get());
            settingRepository.save(setting);
            return value.get();
        });
    }

    @Transactional
    @Override
    public String getString(String name, String value, String description) {
        return getString(name).orElseGet(() -> {
            Setting setting = new Setting();
            setting.setName(name);
            setting.setValue(value);
            setting.setDescription(description);
            settingRepository.save(setting);
            return value;
        });
    }

    @Override
    public Optional<String> getString(String name) {
        Setting setting = settingRepository.findByName(name);
        return setting != null ? Optional.of(setting.getValue()) : Optional.empty();

    }

    @Transactional
    @Override
    public Integer getInteger(String name, int value) {
        return getInteger(name).orElseGet(() -> {
            Setting setting = new Setting();
            setting.setName(name);
            setting.setValue(String.valueOf(value));
            settingRepository.save(setting);
            return value;
        });
    }

    @Override
    public Optional<Integer> getInteger(String name) {
        Setting setting = settingRepository.findByName(name);
        return setting != null ? Optional.of(Integer.valueOf(setting.getValue())) : Optional.empty();

    }

    @Transactional
    @Override
    public Long getLong(String name, long value) {
        return getLong(name).orElseGet(() -> {
            Setting setting = new Setting();
            setting.setName(name);
            setting.setValue(String.valueOf(value));
            settingRepository.save(setting);
            return value;
        });
    }

    @Override
    public Optional<Long> getLong(String name) {
        Setting setting = settingRepository.findByName(name);
        return setting != null ? Optional.of(Long.valueOf(setting.getValue())) : Optional.empty();

    }
}
