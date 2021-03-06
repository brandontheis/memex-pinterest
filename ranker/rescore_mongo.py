#!/usr/bin/env python
# -*- coding: utf-8 -*-

from tqdm import tqdm
from ranker import Ranker
from ui.mongoutils.memex_mongo_utils import MemexMongoUtils
from train import train_on_user_input

def _score_hosts():

    mmu = MemexMongoUtils()
    for host_doc in mmu.list_all_hosts():
        print host_doc
        score = mmu.get_host_score(host_doc["host"])
        mmu.set_host_score(host_doc["host"], score)

def train_and_score_mongo():
    """ Rescore all items from mongo """
    
    print "**************Training*********************"
    train_on_user_input()


    print "**************Scoring and Indexing*****************"
    mmu = MemexMongoUtils()
    docs = mmu.list_all_urls_iterator(return_html = True)

    ranker = Ranker.load()
    for doc in tqdm(docs, leave = True):
        try:
            score = ranker.score_doc(doc)
        except:
            score = 0

        mmu.set_score(doc["url"], score)

    _score_hosts()


if __name__ == '__main__':
   train_and_score_mongo()
