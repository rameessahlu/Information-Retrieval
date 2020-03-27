import data_io
import doc_preprocessing as dpp
import math, operator, itertools, os
from terminaltables import AsciiTable

class Search:
	def __init__(self):
		self.dl_and_pl = data_io.DataIO().deserialize_object(data_io.DICTIONARY_AND_POSTING_LIST_FILE) #dictionary and posting list
		self._pre_processor = dpp.PreProcessor()
	# idf(term) => log[N/df]
	# N = total number of documents in the collection
	# df(term) = total number of documents containing a particular term
	def calculate_tf_idf(self, term):
		return math.log((self.dl_and_pl.doc_length/self.dl_and_pl.dictionary_and_posting_list[term][0]), 10)

	# sim(query,document) => (1 + log(tf(term1)) * log(N/df(term1))) + (1 + log(tf(term2)) * log(N/df(term2))) + ....
	def find_similarity(self, _query):
		document_to_score_mapping = {}
		for term in _query:
			for _posting in self.dl_and_pl.dictionary_and_posting_list[term][1]:
				if _posting[0] not in document_to_score_mapping:
					document_to_score_mapping[_posting[0]] = (1 + math.log(_posting[1]) * self.calculate_tf_idf(term))
				else:
					document_to_score_mapping[_posting[0]] = document_to_score_mapping[_posting[0]] + (1 + math.log(_posting[1]) * self.calculate_tf_idf(term))
		
		document_to_score_mapping = dict( sorted(document_to_score_mapping.items(), key=operator.itemgetter(1),reverse=True))
		return document_to_score_mapping

	def make_query(self, query):
		query	= self._pre_processor.remove_special_character(query)
		query	= self._pre_processor.remove_stop_word(query)
		query	= self._pre_processor.stem(query)
		return self.find_similarity(query)

if __name__ == '__main__':
	_search = Search()
	#print(_search.dl_and_pl.doc_length)

	query = 'They would lose 40 percent of their land and be surrounded, so they would lose free access to the rest of the West Bank.'

	result = _search.make_query(query).items()
	
	sliced_result = dict(itertools.islice(result, 10))
	
	complete_result = [['title', 'main speaker', 'document location' ,'tags', 'similarity score']]

	for doc_id, similarity_score in sliced_result.items():
		complete_result.append([_search.dl_and_pl.meta_data[doc_id][0], _search.dl_and_pl.meta_data[doc_id][1], 
								os.path.join(data_io.DOCS_LOCATION, doc_id + data_io.DOC_EXTENSION),
							   _search.dl_and_pl.meta_data[doc_id][2],  similarity_score])
	
	complete_result = AsciiTable(complete_result)
	print(complete_result.table)