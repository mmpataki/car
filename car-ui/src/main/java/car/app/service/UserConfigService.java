package car.app.service;

import car.engine.util.JsonUtil;
import car.util.SecurityUtil;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserConfigService {

    @Autowired
    DatasetService DS;

    @Data
    @Builder
    public static class UserConfig {
        String user;
        Set<String> pinnedDatasets;
    }

    public void removePinnedDatasets(List<String> pinnedDatasets) throws IOException {
        UserConfig conf;
        try {
            conf = getUserConfig();
            pinnedDatasets.forEach(conf.getPinnedDatasets()::remove);
        } catch (Exception e) {
            conf = UserConfig.builder().pinnedDatasets(new HashSet<>(pinnedDatasets)).build();
        }
        saveUserConfig(conf);
    }

    public List<DatasetService.DatasetSummary> getPinnedDatasets() throws IOException {
        return getUserConfig().getPinnedDatasets().stream().map(
                        dsId -> {
                            try {
                                return DS.makeDatasetSummary(dsId);
                            } catch (Exception e) {
                                return null;
                            }
                        }
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void savePinned(List<String> pinnedDatasets) throws Exception {
        UserConfig conf;
        try {
            conf = getUserConfig();
            conf.getPinnedDatasets().addAll(pinnedDatasets);
        } catch (Exception e) {
            conf = UserConfig.builder().pinnedDatasets(new HashSet<>(pinnedDatasets)).build();
        }
        saveUserConfig(conf);
    }


    public UserConfig getUserConfig() {
        String user = SecurityUtil.getCurrentUser();
        String path = String.format("./%s_user_config.json", user);
        try {
            return JsonUtil.read(path, UserConfig.class);
        } catch (Exception e) {
            return UserConfig.builder().pinnedDatasets(new HashSet<>()).build();
        }
    }

    public void saveUserConfig(UserConfig conf) throws IOException {
        String user = SecurityUtil.getCurrentUser();
        conf.setUser(user);
        JsonUtil.save(String.format("./%s_user_config.json", user), conf);
    }
}
