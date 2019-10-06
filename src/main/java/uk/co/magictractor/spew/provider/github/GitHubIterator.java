package uk.co.magictractor.spew.provider.github;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Splitter;
import com.google.common.collect.Streams;

import uk.co.magictractor.spew.api.ApplicationRequest;
import uk.co.magictractor.spew.api.PageCountServiceIterator;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.example.github.MyGitHubApp;
import uk.co.magictractor.spew.example.github.pojo.GitHubRepository;

/**
 */
public class GitHubIterator<E> extends PageCountServiceIterator<E> {

    private GitHubIterator() {
    }

    @Override
    protected List<E> fetchPage(int pageNumber) {
        ApplicationRequest request = createGetRequest("https://api.github.com/user/repos");

        request.setQueryStringParam("page", pageNumber);
        // Default 30, max 100
        //request.setQueryStringParam("per_page", "1");

        SpewParsedResponse response = request.sendRequest();

        System.err.println(response);

        String linkHeader = response.getHeaderValue("Link");
        System.err.println("link: " + linkHeader);
        if (linkHeader != null) {
            Iterable<String> links = Splitter.on(',').trimResults().split(linkHeader);
            Optional<String> nextLink = Streams.stream(links).filter(link -> link.endsWith("rel=\"next\"")).findAny();
            if (nextLink.isEmpty()) {
                endOfPages();
            }
        }

        return response.getList("$", getElementType());

        // TODO! should use/check the "next" link from the response header
        // "Note: It's important to form calls with Link header values instead of constructing your own URLs."
    }

    public static class GitHubIteratorBuilder<E>
            extends PageCountServiceIteratorBuilder<E, GitHubIterator<E>, GitHubIteratorBuilder<E>> {

        public GitHubIteratorBuilder(SpewApplication<GitHub> application, Class<E> elementType) {
            super(application, elementType, new GitHubIterator<>());
        }
    }

    public static void main(String[] args) {
        Iterator<GitHubRepository> iter = new GitHubIteratorBuilder<>(MyGitHubApp.get(), GitHubRepository.class)
                .build();
        while (iter.hasNext()) {
            GitHubRepository repo = iter.next();
            System.err.println(repo.getName() + " " + repo.getUrl());
        }
    }

}
