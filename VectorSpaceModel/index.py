import data_io
import doc_preprocessing as dpp
import os
from tqdm import tqdm
import dictionary_and_posting

#The format chosen for implementing the dictionary and postings list in the following format:
# +----------+---------------+-----------------------------+
# | term     | doc freq (df) | postings lists              |
# +----------+---------------+-----------------------------+
# | Applause | 6             | (1, 3), (2,9), (35, 8), ... |
# +----------+---------------+-----------------------------+
# | ...      | ...           | ...                         |
# +----------+---------------+-----------------------------+

class Index:

	def __init__(self):
		self.d_io = data_io.DataIO()
		self.pre_processor = dpp.PreProcessor()
		self.total_docs = len(self.d_io.doc_list_fh)
		self.dictionary_and_posting_list = {}
		self.meta_data = {}

	#Calculate term frequency for each word in the document
	#and the resultant term and it's TF pair is further used to compute the document frequency of words and TF-IDF
	def calculate_tf(self, doc_content):
		terms_and_tfs = {}
		for term in doc_content:
			if term not in terms_and_tfs:
				terms_and_tfs[term] = 1
			else:
				terms_and_tfs[term] = terms_and_tfs[term] + 1
		return terms_and_tfs

	def populate_dictionary_and_posting_list(self, terms_and_tfs, doc_id):
		for term, tf in terms_and_tfs.items():
			if term not in self.dictionary_and_posting_list:
				self.dictionary_and_posting_list[term] = [1, [(doc_id, tf)]]
			else:
				self.dictionary_and_posting_list[term] = [self.dictionary_and_posting_list[term][0] + 1, self.dictionary_and_posting_list[term][1] + [(doc_id, tf)]]

	def pre_processing(self, row_in_doc_list):
		doc_loc = os.path.join(data_io.DOCS_LOCATION, str(row_in_doc_list['id']) + data_io.DOC_EXTENSION)
		doc_content = self.d_io.read_txt(doc_loc)
		
		doc_content	= self.pre_processor.remove_special_character(doc_content)
		doc_content	= self.pre_processor.remove_stop_word(doc_content)
		doc_content	= self.pre_processor.stem(doc_content)
		
		#for debugging purpose
		pre_processed_doc = os.path.join(data_io.PRE_PROCESSED_DOCS_LOCATION, str(row_in_doc_list['id']) + data_io.PRE_PROCESSED_DOC_EXTENSION)
		self.d_io.write_json(pre_processed_doc , { 'pre_processed_doc': doc_content } )
		
		return	doc_content

if __name__ == '__main__':
	indexing = Index()

	with tqdm(total=indexing.total_docs) as pbar:
		for row in indexing.d_io.doc_list_fh:
			pre_processed_doc = indexing.pre_processing(row)
			terms_and_tfs = indexing.calculate_tf(pre_processed_doc)
			indexing.populate_dictionary_and_posting_list(terms_and_tfs, row['id'])
			
			indexing.meta_data[row['id']] = [row['title'], row['main_speaker'], row['tags']] 
			pbar.update(1)
			#break
	d_p_obj = dictionary_and_posting.DictionaryAndPosting(indexing.dictionary_and_posting_list, indexing.total_docs, indexing.meta_data)
	indexing.d_io.serialize_object(data_io.DICTIONARY_AND_POSTING_LIST_FILE, d_p_obj)