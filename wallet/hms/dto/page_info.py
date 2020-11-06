# -*- coding: utf-8 -*- 
# @Author    :
# @Date      :2020/6/11
# @Version   :Python 3.8.3
# @File      :page_info


class PageInfo(object):
    def __init__(self):
        self.pageSize = None
        self.nextSession = None

    def set_pageSize(self, pageSize):
        self.pageSize = pageSize

    def get_pageSize(self):
        return self.pageSize

    def set_nextSession(self, nextSession):
        self.nextSession = nextSession

    def get_nextSession(self):
        return self.nextSession
