package com.aditi.githubreviewbot.analysis.parser.impl;

import com.aditi.githubreviewbot.analysis.model.Issue;
import com.aditi.githubreviewbot.analysis.parser.ToolParser;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class SpotBugsXmlParser implements ToolParser {

    @Override
    public List<Issue> parse(String repoPath) {
        List<Issue> issues = new ArrayList<>();

        try {
            File spotBugsReportsFile = new File(repoPath, "target/spotbugsXml.xml");

            if (!spotBugsReportsFile.exists()) return issues;

            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(spotBugsReportsFile);

            NodeList bugInstances = document.getElementsByTagName("BugInstance");

            for (int i = 0; i < bugInstances.getLength(); i++) {
                Element bugElement = (Element) bugInstances.item(i);

                Element sourceLineElement = (Element) bugElement.getElementsByTagName("SourceLine").item(0);

                if (sourceLineElement == null) continue;

                String filePath = sourceLineElement.getAttribute("sourcepath");

                String startLineAttribute = sourceLineElement.getAttribute("start");
                int lineNumber = startLineAttribute.isEmpty()
                        ? 0
                        : Integer.parseInt(startLineAttribute);

                String bugType = bugElement.getAttribute("type");

                var shortMessageNode = bugElement.getElementsByTagName("ShortMessage").item(0);

                String shortMessage = (shortMessageNode != null) ? shortMessageNode.getTextContent() : "";

                String message = bugType + (shortMessage.isEmpty() ? "" : " : " + shortMessage);

                Issue issue = new Issue();
                issue.setFile(filePath);
                issue.setLine(lineNumber);
                issue.setColumn(0);
                issue.setMessage(message);
                issue.setRuleId(bugType);
                issues.add(issue);
}

        } catch (Exception e) {
            e.printStackTrace();
        }
        return issues;
    }
}
