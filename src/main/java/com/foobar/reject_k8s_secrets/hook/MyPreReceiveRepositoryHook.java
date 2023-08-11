package com.foobar.reject_k8s_secrets.hook;

import com.atlassian.bitbucket.hook.repository.*;
import com.atlassian.bitbucket.repository.*;
import com.atlassian.bitbucket.scm.ScmService;
import com.atlassian.bitbucket.scm.bulk.BulkContentCallback;
import com.atlassian.bitbucket.scm.bulk.BulkContentCommandParameters;
import com.atlassian.bitbucket.scm.bulk.ScmBulkContentCommandFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.foobar.reject_k8s_secrets.parser.YamlParser;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class MyPreReceiveRepositoryHook implements PreRepositoryHook<RepositoryHookRequest> {
    private final ScmService scmService;

    private String failedFile = null;

    public MyPreReceiveRepositoryHook(@ComponentImport final ScmService scmService) {
        this.scmService = scmService;
    }

    @Nonnull
    @Override
    public RepositoryHookResult preUpdate(@Nonnull PreRepositoryHookContext preRepositoryHookContext, @Nonnull RepositoryHookRequest repositoryHookRequest) {
        ScmBulkContentCommandFactory bulkContentCommandFactory = scmService.getBulkContentCommandFactory(repositoryHookRequest.getRepository());

        for (RefChange refChange : repositoryHookRequest.getRefChanges()) {
            BulkContentCommandParameters parameters = new BulkContentCommandParameters
                    .Builder(refChange.getToHash())
                    .sinceCommitId(refChange.getFromHash())
                    .build();

            BulkContentCallback bulkContentCallback = (bulkFile, inputStream) -> {
                String filePathLc = bulkFile.getPath().toLowerCase();

                if (filePathLc.endsWith(".yml") || filePathLc.endsWith(".yaml")) {
                    String text = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                            .lines()
                            .collect(Collectors.joining("\n"));

                    if (!YamlParser.parse(text)) {
                        failedFile = bulkFile.getPath();
                    }
                }
            };

            bulkContentCommandFactory.contents(parameters, bulkContentCallback).call();
        }

        return failedFile != null ?
                RepositoryHookResult.rejected("Secrets found!", String.format("File [ %s ] contains secret data.", failedFile)) :
                RepositoryHookResult.accepted();
    }
}
