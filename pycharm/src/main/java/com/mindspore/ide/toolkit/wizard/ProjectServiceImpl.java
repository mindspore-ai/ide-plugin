package com.mindspore.ide.toolkit.wizard;

import com.intellij.openapi.project.Project;
import com.mindspore.ide.toolkit.common.enums.EnumError;
import com.mindspore.ide.toolkit.common.exceptions.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.AccessControlException;
import java.util.Optional;

public class ProjectServiceImpl implements ProjectService {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private static final String PROJECT_SERVICE = "PROJECT_SERVICE";

    private static final String CACHE_MINDSPORE_DIR = ".mindspore";

    private Project project;

    private ProjectServiceImpl(Project project) {
        this.project = project;
    }

    @Override
    public String createCacheMindSporeDir(String parentDirPath) throws BizException {
        File cacheDir = new File(parentDirPath + File.separator + CACHE_MINDSPORE_DIR);
        try {
            boolean suc = cacheDir.mkdir();
            if (!suc && !cacheDir.exists()) {
                throw new AccessControlException("can not create download file due to the parent dir not find");
            }
            return cacheDir.getCanonicalPath();
        } catch (IOException exception) {
            LOG.error(exception.getMessage(), exception);
            throw new BizException(EnumError.CREATE_CACHE_DIR_FAIL.getErrCode(), EnumError.CREATE_CACHE_DIR_FAIL.getErrMsg());
        }
    }

    public static ProjectService getInstance(Project project) throws BizException {
        if (project == null) {
            throw new BizException(EnumError.NULL_PROJECT.getErrMsg());
        }
        Optional<ProjectServiceImpl> projectServiceOp = MindSporeManager.INSTANCE.get(PROJECT_SERVICE + project.getBasePath(), ProjectServiceImpl.class);
        if (projectServiceOp.isPresent()) {
            return projectServiceOp.get();
        } else {
            ProjectService projectService = new ProjectServiceImpl(project);
            return projectService;
        }
    }
}
