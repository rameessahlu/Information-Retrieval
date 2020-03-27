import csv, json, os, pickle

DATA_FOLDER = 'data'
DOCS_LOCATION = os.path.join(DATA_FOLDER, 'docs')
DOC_LIST = os.path.join(DATA_FOLDER, 'doc_list.csv')

RESULTS_FOLDER = 'results'
PRE_PROCESSED_DOCS_LOCATION = os.path.join(RESULTS_FOLDER, 'pre_processed_docs')

DOC_EXTENSION = '.txt'
PRE_PROCESSED_DOC_EXTENSION = '.json'

DICTIONARY_AND_POSTING_LIST_FILE = os.path.join(RESULTS_FOLDER, 'dictionary_and_posting_list.pickle')

class DataIO:

	def __init__(self):
		self.doc_list_fh = self.read_csv(DOC_LIST)

	def read_csv(self, file_name):
		return list(csv.DictReader(open(file_name, encoding="utf8")))

	def read_txt(self, file_name):
		file = open(file_name, mode='r', encoding="utf8")
		content = file.read()
		file.close()
		return content

	def read_json(self, doc_name):
		with open(doc_name, 'r') as fp:
			return json.load(fp)

	def write_json(self, doc_name, doc_content):
		with open(doc_name, 'w') as fp:
			json.dump(doc_content, fp, sort_keys=True, indent=4)

	def serialize_object(self, file_name, obj):
		with open(file_name, 'wb') as handle:
			pickle.dump(obj, handle, protocol=pickle.HIGHEST_PROTOCOL)

	def deserialize_object(self, file_name):
		with open(file_name, 'rb') as handle:
			return pickle.load(handle)