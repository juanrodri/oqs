/*
 * $Id: Paginator.java 56 2008-03-02 13:50:21Z alex@opoo.org $
 *
 * Copyright 2006-2008 Alex Lin. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opoo.dao.support;

import java.util.ArrayList;
import java.util.List;

import org.opoo.util.Assert;

/**
 *
 *
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public class Paginator {
    private Pageable pageable;
    private int pageCount = -1;
    private Page pages[];
    public Paginator(Pageable pageable) {
        String msg = "无法处理分页";
        Assert.notNull(pageable, msg);
        this.pageable = pageable;
        pageCount = (int) Math.ceil((double) pageable.getItemCount()
                                    / (double) pageable.getPageSize());
    }

    public int getStartIndex() {
        return pageable.getStartIndex();
    }

    public int getPageIndex() {
        if (pageable.getPageSize() == 0) {
            return 0;
        } else {
            return pageable.getStartIndex() / pageable.getPageSize();
        }
    }

    public int getPageSize() {
        return pageable.getPageSize();
    }

    public int getPageCount() {
        return pageCount;
    }

    public int getItemCount() {
        return pageable.getItemCount();
    }

    public boolean hasPreviousPage() {
        return getPageIndex() > 0;
    }

    public int getPreviousPageStartIndex() {
        return (getPageIndex() - 1) * pageable.getPageSize();
    }

    public boolean hasNextPage() {
        return getPageIndex() + 1 < getPageCount();
    }
    public boolean isHasNext(){
	return hasNextPage();
    }
    public boolean isHasPrevious(){
	return hasPreviousPage();
    }

    public int getNextPageStartIndex() {
        return (getPageIndex() + 1) * pageable.getPageSize();
    }

    public int getLastPageStartIndex() {
        return (getPageCount() - 1) * pageable.getPageSize();
    }

    public Page[] getPages() {
        return getPages(10);
    }

    public Page[] getPages(int numViewablePages) {
        if (pages == null) {
            int startIndex = getPageIndex();
            int maxPages = pageCount >= numViewablePages ? numViewablePages :
                           pageCount;
            List pageList = new ArrayList(maxPages);
            int i = 0;
            do {
                if (i >= maxPages) {
                    break;
                }
                Page page = new Page();
                page.setNumber(i + startIndex + 1);
                page.setStart((i + startIndex) * pageable.getPageSize());
                pageList.add(page);
                if (i + startIndex + 2 > pageCount) {
                    break;
                }
                i++;
            } while (true);
            if (pageList.size() > 0) {
                Page lowestPage;
                Page page;
                for (lowestPage = (Page) pageList.get(0);
                                  pageList.size() < maxPages &&
                                  lowestPage.getStart() != 0; lowestPage = page) {
                    page = Page.getPrevious(lowestPage, pageable.getPageSize());
                    pageList.add(0, page);
                }

                int count = 2;
                do {
                    if (count-- <= 0 || lowestPage.getStart() == 0) {
                        break;
                    }
                    page = Page.getPrevious(lowestPage, pageable.getPageSize());
                    pageList.add(0, page);
                    lowestPage = page;
                    if (count == 0 && lowestPage.getNumber() == 2) {
                        count++;
                    }
                } while (true);
                if (lowestPage.getNumber() - 1 >= 2) {
                    pageList.add(0, null);
                    pageList.add(0, Page.FIRST);
                }
            }
            pages = (Page[]) pageList.toArray(new Page[0]);
        }
        return pages;
    }

    public static void main(String[] args) {
	Pageable p = new Pageable(){
            public int getItemCount() {
                return 16020;
            }

            public int getPageSize() {
                return 10;
            }

            public int getStartIndex() {
                return 660;
            }
        };
	Paginator pp = new Paginator(p);
	Page[] pages = pp.getPages(10);
	System.out.println(pages.length);
	for(int i = 0 ; i < pages.length ; i++){
	    System.out.println(pages[i] != null ? pages[i].getNumber() : 0);
	}
	System.out.println(pages);
    }
}
