# information_retrieval_system
Command-line based Information Retrieval System using Apache Lucene

--> This project is part of Infromation Retrieval course at Otto Von Guericke University, Magdeburg - 2019
(http://www.dke-research.de/findke/en/Studies/Courses/Winter+Term+2019_2020/Information+Retrieval-p-1180.html)

This system satisfies below mentioned requirements:

1) Uses Apache Lucene(version 8.3.0), to parse and index Plain Text and HTML documents that a given folderand its subfolders contain. 
2) List all parsed files.
3) Considers the English language and uses a stemmer(Porter Stemmer).
4) Selects an available search index and performs earch operation.
5) Search's multiple fields concurrently: not only search the document's text (body tag), but also its title and date (for HTML documents).
6) Prints a ranked list of relevant articles given a search query. The output contains the most relevant documents, their rank, path, last modification time, relevance score and in addition for HTML documents title and summary.
