package top.easelink.lcg.ui.main.articles.view;

import java.util.List;

import top.easelink.lcg.ui.main.source.model.ForumThread;

public interface FourmArticleNavigator extends ArticlesNavigator {
    void setUpTabLayout(List<ForumThread> forumThreadList);
}
