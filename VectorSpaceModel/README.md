
# Vector Space Search Engine!

This is a python implementation of Vector space model. It's one of the similarity-based method among many information retrieval methods in Search Engine.

###  1. Document processing and indexing
 - Implemented a stop list and a stemmer to pre-process the documents.
 - Implemented dictionary and postings lists in the following format:

 term | doc freq (df) | postings lists
--- | --- | --- 
Applause | 6 | (1, 3), (2,9), (35, 8), ... 
... | ... | ... 

### 2. Vector Space model

- Here the program provides a TF-IDF-based ranking for the documents.
- I found VSM with TF-IDF is effective than other weighting methods. 

### 3. Web Interface
*yet to complete*



#  Instruction
- **Indexing**
> python index.py
- **Searching**
> python search.py