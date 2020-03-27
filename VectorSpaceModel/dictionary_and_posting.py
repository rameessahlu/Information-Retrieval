class DictionaryAndPosting:
	def __init__(self, dictionary_and_posting_list, doc_length, meta_data):
		self.dictionary_and_posting_list = dictionary_and_posting_list #Contains df and tf with document ids and term itself.
		self.doc_length = doc_length #Total number of documents in the collection
		self.meta_data = meta_data