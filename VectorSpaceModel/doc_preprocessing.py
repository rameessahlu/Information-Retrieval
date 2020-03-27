import nltk
from nltk.corpus import stopwords
from nltk.tokenize import RegexpTokenizer
from nltk.tokenize import word_tokenize 
from nltk.stem import PorterStemmer 

import string

STOP_WORDS = set(stopwords.words('english'))
PS = PorterStemmer() 

class PreProcessor:

	def __init__(self):
		pass

	def remove_stop_word(self, doc_content):
		word_tokens = word_tokenize(doc_content) 
		filtered_doc = [term for term in word_tokens if not term in STOP_WORDS] 
		return filtered_doc

	def remove_special_character(self, doc_content):
		doc_content = doc_content.encode('ascii', 'ignore').decode('ascii')
		return doc_content.translate(str.maketrans('','',string.punctuation))
	
	def stem(self, doc_content):
		stemmed_doc = []
		for term in doc_content: 
			stemmed_doc.append(PS.stem(term))
		return stemmed_doc